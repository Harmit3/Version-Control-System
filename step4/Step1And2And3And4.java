import java.io.*;
import java.util.*;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.commons.text.similarity.CosineSimilarity;

public class Step1And2And3And4 {
    
    private static final int CONTEXT_SIZE = 4;
    private static final int CANDIDATE_SIZE = 15;
    private static final double CONTENT_WEIGHT = 0.6;
    private static final double CONTEXT_WEIGHT = 0.4;
    private static final double THRESHOLD = 0.5;
    
    private static final LevenshteinDistance levenshtein = new LevenshteinDistance();
    private static final CosineSimilarity cosine = new CosineSimilarity();
    
    private List<String> oldLinesOriginal;
    private List<String> newLinesOriginal;
    private List<String> oldLinesProcessed;
    private List<String> newLinesProcessed;
    
    private Map<Integer, Integer> unchangedMapping;
    private List<Integer> leftList;
    private List<Integer> rightList;
    
    private Map<Integer, List<Candidate>> candidateMap;
    
    private Map<Integer, Integer> resolvedMapping;
    
    public Step1And2And3And4(String oldFile, String newFile) throws IOException {
        this.oldLinesOriginal = readFile(oldFile);
        this.newLinesOriginal = readFile(newFile);
        this.unchangedMapping = new HashMap<>();
        this.leftList = new ArrayList<>();
        this.rightList = new ArrayList<>();
        this.candidateMap = new HashMap<>();
        this.resolvedMapping = new HashMap<>();
    }
    
    public void run() {
        System.out.println("=== LHDiff: Steps 1 + 2 + 3 + 4 ===\n");
        
        System.out.println("STEP 1: Preprocessing...");
        preprocessFiles();
        System.out.println("Complete\n");
        
        System.out.println("STEP 2: Unix Diff (LCS)...");
        applyUnixDiff();
        filterBracketOnlyMappings();
        System.out.println("Complete");
        System.out.println("  Unchanged: " + unchangedMapping.size());
        System.out.println("  Left List: " + leftList.size());
        System.out.println("  Right List: " + rightList.size() + "\n");
        
        System.out.println("STEP 3: Generating Candidates...");
        generateCandidates();
        System.out.println("Complete\n");
        
        System.out.println("STEP 4: Resolving Conflicts...");
        resolveConflicts();
        System.out.println("Complete");
        System.out.println("  Resolved matches: " + resolvedMapping.size() + "\n");
    }
    
    private void preprocessFiles() {
        oldLinesProcessed = new ArrayList<>();
        newLinesProcessed = new ArrayList<>();
        
        for (String line : oldLinesOriginal) {
            oldLinesProcessed.add(preprocessLine(line));
        }
        for (String line : newLinesOriginal) {
            newLinesProcessed.add(preprocessLine(line));
        }
    }
    
    private String preprocessLine(String line) {
        return line.trim().replaceAll("\\s+", " ").toLowerCase();
    }
    
    private void applyUnixDiff() {
        int[][] lcs = computeLCS(oldLinesProcessed, newLinesProcessed);
        backtrackLCS(lcs, oldLinesProcessed.size(), newLinesProcessed.size());
        buildChangedLists();
    }
    
    private void filterBracketOnlyMappings() {
        Map<Integer, Integer> filtered = new HashMap<>();
        
        for (Map.Entry<Integer, Integer> entry : unchangedMapping.entrySet()) {
            int oldIdx = entry.getKey();
            int newIdx = entry.getValue();
            
            if (!isBracketOnly(oldLinesOriginal.get(oldIdx)) && 
                !isBracketOnly(newLinesOriginal.get(newIdx))) {
                filtered.put(oldIdx, newIdx);
            }
        }
        
        unchangedMapping = filtered;
    }
    
    private int[][] computeLCS(List<String> old, List<String> newList) {
        int m = old.size();
        int n = newList.size();
        int[][] lcs = new int[m + 1][n + 1];
        
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (old.get(i - 1).equals(newList.get(j - 1))) {
                    lcs[i][j] = lcs[i - 1][j - 1] + 1;
                } else {
                    lcs[i][j] = Math.max(lcs[i - 1][j], lcs[i][j - 1]);
                }
            }
        }
        return lcs;
    }
    
    private void backtrackLCS(int[][] lcs, int i, int j) {
        if (i == 0 || j == 0) return;
        
        if (oldLinesProcessed.get(i - 1).equals(newLinesProcessed.get(j - 1))) {
            backtrackLCS(lcs, i - 1, j - 1);
            unchangedMapping.put(i - 1, j - 1);
        } else if (lcs[i - 1][j] >= lcs[i][j - 1]) {
            backtrackLCS(lcs, i - 1, j);
        } else {
            backtrackLCS(lcs, i, j - 1);
        }
    }
    
    private void buildChangedLists() {
        Set<Integer> matchedOld = unchangedMapping.keySet();
        Set<Integer> matchedNew = new HashSet<>(unchangedMapping.values());
        
        for (int i = 0; i < oldLinesOriginal.size(); i++) {
            if (!matchedOld.contains(i)) leftList.add(i);
        }
        for (int i = 0; i < newLinesOriginal.size(); i++) {
            if (!matchedNew.contains(i)) rightList.add(i);
        }
    }
    
    private void generateCandidates() {
        for (int leftIdx : leftList) {
            if (isBracketOnly(oldLinesOriginal.get(leftIdx))) {
                continue;
            }
            
            List<Candidate> candidates = new ArrayList<>();
            
            String leftContent = oldLinesProcessed.get(leftIdx);
            String leftContext = getContext(oldLinesProcessed, leftIdx);
            
            for (int rightIdx : rightList) {
                if (isBracketOnly(newLinesOriginal.get(rightIdx))) {
                    continue;
                }
                
                String rightContent = newLinesProcessed.get(rightIdx);
                String rightContext = getContext(newLinesProcessed, rightIdx);
                
                double contentSim = calculateContentSimilarity(leftContent, rightContent);
                double contextSim = calculateContextSimilarity(leftContext, rightContext);
                double combinedSim = CONTENT_WEIGHT * contentSim + CONTEXT_WEIGHT * contextSim;
                
                candidates.add(new Candidate(rightIdx, combinedSim, contentSim, contextSim));
            }
            
            candidates.sort((a, b) -> Double.compare(b.combinedSimilarity, a.combinedSimilarity));
            if (candidates.size() > CANDIDATE_SIZE) {
                candidates = candidates.subList(0, CANDIDATE_SIZE);
            }
            
            candidateMap.put(leftIdx, candidates);
        }
    }
    
    private void resolveConflicts() {
        Map<Integer, Integer> newLineOwner = new HashMap<>();
        Map<Integer, Double> newLineScore = new HashMap<>();
        
        for (int oldIdx : leftList) {
            if (isBracketOnly(oldLinesOriginal.get(oldIdx))) {
                continue;
            }
            
            List<Candidate> candidates = candidateMap.get(oldIdx);
            
            if (candidates == null || candidates.isEmpty()) continue;
            
            Candidate bestCandidate = null;
            double bestScore = 0.0;
            
            for (Candidate candidate : candidates) {
                if (isBracketOnly(newLinesOriginal.get(candidate.lineIndex))) {
                    continue;
                }
                
                String oldContent = oldLinesProcessed.get(oldIdx);
                String oldContext = getContext(oldLinesProcessed, oldIdx);
                String newContent = newLinesProcessed.get(candidate.lineIndex);
                String newContext = getContext(newLinesProcessed, candidate.lineIndex);
                
                double contentSim = calculateContentSimilarity(oldContent, newContent);
                double contextSim = calculateContextSimilarity(oldContext, newContext);
                double combinedSim = CONTENT_WEIGHT * contentSim + CONTEXT_WEIGHT * contextSim;
                
                if (combinedSim > bestScore && combinedSim >= THRESHOLD) {
                    bestScore = combinedSim;
                    bestCandidate = new Candidate(candidate.lineIndex, combinedSim, contentSim, contextSim);
                }
            }
            
            if (bestCandidate == null) continue;
            
            int newIdx = bestCandidate.lineIndex;
            
            if (newLineOwner.containsKey(newIdx)) {
                int competingOldIdx = newLineOwner.get(newIdx);
                double competingScore = newLineScore.get(newIdx);
                
                if (bestScore > competingScore) {
                    resolvedMapping.remove(competingOldIdx);
                    resolvedMapping.put(oldIdx, newIdx);
                    newLineOwner.put(newIdx, oldIdx);
                    newLineScore.put(newIdx, bestScore);
                }
            } else {
                resolvedMapping.put(oldIdx, newIdx);
                newLineOwner.put(newIdx, oldIdx);
                newLineScore.put(newIdx, bestScore);
            }
        }
    }
    
    private double calculateContentSimilarity(String s1, String s2) {
        Integer distance = levenshtein.apply(s1, s2);
        if (distance == null) return 0.0;
        int maxLen = Math.max(s1.length(), s2.length());
        if (maxLen == 0) return 1.0;
        return 1.0 - ((double) distance / maxLen);
    }
    
    private double calculateContextSimilarity(String s1, String s2) {
        Map<CharSequence, Integer> map1 = getWordMap(s1);
        Map<CharSequence, Integer> map2 = getWordMap(s2);
        
        if (map1.isEmpty() || map2.isEmpty()) return 0.0;
        
        return cosine.cosineSimilarity(map1, map2);
    }
    
    private Map<CharSequence, Integer> getWordMap(String text) {
        Map<CharSequence, Integer> map = new HashMap<>();
        if (text == null || text.isEmpty()) return map;
        
        String[] words = text.split("\\s+");
        for (String word : words) {
            if (!word.isEmpty()) {
                map.put(word, map.getOrDefault(word, 0) + 1);
            }
        }
        return map;
    }
    
    private String getContext(List<String> lines, int idx) {
        StringBuilder context = new StringBuilder();
        int start = Math.max(0, idx - CONTEXT_SIZE);
        int end = Math.min(lines.size(), idx + CONTEXT_SIZE + 1);
        
        for (int i = start; i < end; i++) {
            if (i != idx) {
                context.append(lines.get(i)).append(" ");
            }
        }
        return context.toString().trim();
    }
    
    private boolean isBracketOnly(String line) {
        String trimmed = line.trim();
        return trimmed.equals("}") || trimmed.equals("{");
    }
    
    public void printResults() {
        System.out.println("\n=== FINAL RESULTS ===\n");
        
        Map<Integer, Integer> allMappings = new HashMap<>();
        allMappings.putAll(unchangedMapping);
        allMappings.putAll(resolvedMapping);
        
        System.out.println("LINE MAPPINGS (Old -> New, -1 = unmapped):");
        for (int i = 0; i < oldLinesOriginal.size(); i++) {
            // FIXED: Skip bracket-only lines completely
            if (isBracketOnly(oldLinesOriginal.get(i))) {
                continue;
            }
            if (allMappings.containsKey(i)) {
                System.out.printf("%d -> %d\n", i + 1, allMappings.get(i) + 1);
            } else {
                System.out.printf("%d -> -1\n", i + 1);
            }
        }
        
        System.out.println("\nDETAILED STEP 4 RESULTS:");
        System.out.println("------------------------");
        for (int oldIdx : resolvedMapping.keySet()) {
            int newIdx = resolvedMapping.get(oldIdx);
            System.out.printf("Old Line %d: %s\n", oldIdx + 1, oldLinesOriginal.get(oldIdx).trim());
            System.out.printf("  → New Line %d: %s\n", newIdx + 1, newLinesOriginal.get(newIdx).trim());
            System.out.println();
        }
        
        Set<Integer> matchedOld = allMappings.keySet();
        Set<Integer> matchedNew = new HashSet<>(allMappings.values());
        
        System.out.println("DELETED LINES (Old file, not mapped):");
        for (int i = 0; i < oldLinesOriginal.size(); i++) {
            // FIXED: Skip bracket-only lines
            if (!matchedOld.contains(i) && !isBracketOnly(oldLinesOriginal.get(i))) {
                System.out.printf("Line %d: %s\n", i + 1, oldLinesOriginal.get(i).trim());
            }
        }
        
        System.out.println("\nADDED LINES (New file, not mapped):");
        for (int i = 0; i < newLinesOriginal.size(); i++) {
            // FIXED: Skip bracket-only lines
            if (!matchedNew.contains(i) && !isBracketOnly(newLinesOriginal.get(i))) {
                System.out.printf("Line %d: %s\n", i + 1, newLinesOriginal.get(i).trim());
            }
        }
    }
    
    public void saveResults(String outputFile) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            writer.println("=== LHDiff COMPLETE RESULTS (Steps 1-4) ===");
            writer.println();
            
            writer.println("SUMMARY:");
            writer.println("--------");
            writer.println("Total old lines: " + oldLinesOriginal.size());
            writer.println("Total new lines: " + newLinesOriginal.size());
            writer.println("Unchanged lines (Step 2): " + unchangedMapping.size());
            writer.println("Resolved matches (Step 4): " + resolvedMapping.size());
            writer.println("Threshold: " + THRESHOLD);
            writer.println();
            
            Map<Integer, Integer> allMappings = new HashMap<>();
            allMappings.putAll(unchangedMapping);
            allMappings.putAll(resolvedMapping);
            
            writer.println("ALL LINE MAPPINGS (Old -> New, -1 = unmapped):");
            writer.println("------------------------------------------------");
            for (int i = 0; i < oldLinesOriginal.size(); i++) {
                // FIXED: Skip bracket-only lines completely
                if (isBracketOnly(oldLinesOriginal.get(i))) {
                    continue;
                }
                if (allMappings.containsKey(i)) {
                    writer.printf("%d -> %d\n", i + 1, allMappings.get(i) + 1);
                } else {
                    writer.printf("%d -> -1\n", i + 1);
                }
            }
            writer.println();
            
            writer.println("STEP 2: UNCHANGED LINES");
            writer.println("------------------------");
            List<Integer> unchangedSorted = new ArrayList<>(unchangedMapping.keySet());
            Collections.sort(unchangedSorted);
            for (int oldIdx : unchangedSorted) {
                int newIdx = unchangedMapping.get(oldIdx);
                writer.printf("%d -> %d: %s\n", oldIdx + 1, newIdx + 1, 
                            oldLinesOriginal.get(oldIdx).trim());
            }
            writer.println();
            
            writer.println("STEP 4: RESOLVED MATCHES");
            writer.println("------------------------");
            List<Integer> resolvedSorted = new ArrayList<>(resolvedMapping.keySet());
            Collections.sort(resolvedSorted);
            for (int oldIdx : resolvedSorted) {
                int newIdx = resolvedMapping.get(oldIdx);
                writer.printf("Old Line %d: %s\n", oldIdx + 1, oldLinesOriginal.get(oldIdx).trim());
                writer.printf("New Line %d: %s\n", newIdx + 1, newLinesOriginal.get(newIdx).trim());
                writer.println();
            }
            
            Set<Integer> matchedOld = allMappings.keySet();
            Set<Integer> matchedNew = new HashSet<>(allMappings.values());
            
            writer.println("UNMATCHED OLD LINES (Deleted):");
            writer.println("-------------------------------");
            for (int i = 0; i < oldLinesOriginal.size(); i++) {
                // FIXED: Skip bracket-only lines
                if (!matchedOld.contains(i) && !isBracketOnly(oldLinesOriginal.get(i))) {
                    writer.printf("Line %d: %s\n", i + 1, oldLinesOriginal.get(i).trim());
                }
            }
            writer.println();
            
            writer.println("UNMATCHED NEW LINES (Added):");
            writer.println("-----------------------------");
            for (int i = 0; i < newLinesOriginal.size(); i++) {
                // FIXED: Skip bracket-only lines
                if (!matchedNew.contains(i) && !isBracketOnly(newLinesOriginal.get(i))) {
                    writer.printf("Line %d: %s\n", i + 1, newLinesOriginal.get(i).trim());
                }
            }
        }
        System.out.println("Results saved to: " + outputFile);
    }
    
    private List<String> readFile(String path) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                lines.add(line);
            }
        }
        return lines;
    }
    
    public static class Candidate {
        int lineIndex;
        double combinedSimilarity;
        double contentSimilarity;
        double contextSimilarity;
        
        public Candidate(int lineIndex, double combined, double content, double context) {
            this.lineIndex = lineIndex;
            this.combinedSimilarity = combined;
            this.contentSimilarity = content;
            this.contextSimilarity = context;
        }
    }
    
    public static void main(String[] args) {
        if (args.length < 2) {
            return;
        }
        
        String oldFile = args[0];
        String newFile = args[1];
        String outputFile = args.length > 2 ? args[2] : "results.txt";
        
        try {
            Step1And2And3And4 program = new Step1And2And3And4(oldFile, newFile);
            program.run();
            program.printResults();
            program.saveResults(outputFile);
            
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
 import java.io.*;
import java.util.*;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.commons.text.similarity.CosineSimilarity;

/**
 * Complete LHDiff with FINAL FIXED Step 5: Line Split Detection
 * Checks ALL lines (including mapped ones) but only uses UNMAPPED new lines
 */
public class CompleteLHDiff {
    
    private static final int CONTEXT_SIZE = 4;
    private static final int CANDIDATE_SIZE = 15;
    private static final double CONTENT_WEIGHT = 0.6;
    private static final double CONTEXT_WEIGHT = 0.4;
    private static final double THRESHOLD = 0.5;
    private static final double SPLIT_THRESHOLD = 0.60; // Lower for splits
    private static final int MAX_SPLIT_LINES = 6;
    
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
    private Map<Integer, List<Integer>> lineSplits;
    private Map<Integer, Double> mappingScores;
    
    public CompleteLHDiff(String oldFile, String newFile) throws IOException {
        this.oldLinesOriginal = readFile(oldFile);
        this.newLinesOriginal = readFile(newFile);
        this.unchangedMapping = new HashMap<>();
        this.leftList = new ArrayList<>();
        this.rightList = new ArrayList<>();
        this.candidateMap = new HashMap<>();
        this.resolvedMapping = new HashMap<>();
        this.lineSplits = new HashMap<>();
        this.mappingScores = new HashMap<>();
    }
    
    public void run() {
        
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
        
        System.out.println("STEP 5: Detecting Line Splits...");
        detectLineSplits();
        System.out.println("Complete");
        System.out.println("  Line splits found: " + lineSplits.size() + "\n");
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
                mappingScores.put(oldIdx, 1.0);
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
            if (isBracketOnly(oldLinesOriginal.get(leftIdx))) continue;
            
            List<Candidate> candidates = new ArrayList<>();
            String leftContent = oldLinesProcessed.get(leftIdx);
            String leftContext = getContext(oldLinesProcessed, leftIdx);
            
            for (int rightIdx : rightList) {
                if (isBracketOnly(newLinesOriginal.get(rightIdx))) continue;
                
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
            if (isBracketOnly(oldLinesOriginal.get(oldIdx))) continue;
            
            List<Candidate> candidates = candidateMap.get(oldIdx);
            if (candidates == null || candidates.isEmpty()) continue;
            
            Candidate bestCandidate = null;
            double bestScore = 0.0;
            
            for (Candidate candidate : candidates) {
                if (isBracketOnly(newLinesOriginal.get(candidate.lineIndex))) continue;
                
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
                    mappingScores.remove(competingOldIdx);
                    resolvedMapping.put(oldIdx, newIdx);
                    mappingScores.put(oldIdx, bestScore);
                    newLineOwner.put(newIdx, oldIdx);
                    newLineScore.put(newIdx, bestScore);
                }
            } else {
                resolvedMapping.put(oldIdx, newIdx);
                mappingScores.put(oldIdx, bestScore);
                newLineOwner.put(newIdx, oldIdx);
                newLineScore.put(newIdx, bestScore);
            }
        }
    }
    
    /*
     * Check ALL old lines (both mapped and unmapped)
     * Only use UNMAPPED new lines for splits
     * Replace existing mapping if split is significantly better
     */
    private void detectLineSplits() {
        // Get current mappings from Steps 2 & 4
        Set<Integer> reservedNewLines = new HashSet<>(unchangedMapping.values());
        reservedNewLines.addAll(resolvedMapping.values());
        
        // Check ALL old lines (not just unmapped)
        for (int oldIdx = 0; oldIdx < oldLinesOriginal.size(); oldIdx++) {
            if (isBracketOnly(oldLinesOriginal.get(oldIdx))) continue;
            
            String oldLine = oldLinesProcessed.get(oldIdx);
            double currentScore = mappingScores.getOrDefault(oldIdx, 0.0);
            
            List<Integer> bestSplit = null;
            double bestSplitSimilarity = 0.0;
            
            // Try each possible starting position in new file
            for (int startNewIdx = 0; startNewIdx < newLinesOriginal.size(); startNewIdx++) {
                if (isBracketOnly(newLinesOriginal.get(startNewIdx))) continue;
                
                // Skip if this line is reserved by Step 2 (unchangedMapping)
                // We only want to potentially use lines from Step 4 or unmapped lines
                if (unchangedMapping.containsValue(startNewIdx)) continue;
                
                // Try combining consecutive lines starting here
                SplitCandidate candidate = tryConsecutiveCombination(
                    oldLine, startNewIdx, unchangedMapping.values());
                
                if (candidate != null && candidate.lines.size() > 1) {
                    if (candidate.similarity > bestSplitSimilarity) {
                        bestSplitSimilarity = candidate.similarity;
                        bestSplit = new ArrayList<>(candidate.lines);
                    }
                }
            }
            
            // If we found a split that's better than current mapping
            if (bestSplit != null && bestSplitSimilarity >= SPLIT_THRESHOLD) {
                // Use split if: no current mapping OR split is better
                if (currentScore == 0.0 || bestSplitSimilarity > currentScore) {
                    // Free up the old single-line mapping if it exists
                    if (resolvedMapping.containsKey(oldIdx)) {
                        resolvedMapping.remove(oldIdx);
                    }
                    
                    // Create the split
                    lineSplits.put(oldIdx, bestSplit);
                    mappingScores.put(oldIdx, bestSplitSimilarity);
                    
                    System.out.println("DEBUG: Found split for line " + (oldIdx+1) + 
                        " -> " + bestSplit + " with similarity " + bestSplitSimilarity);
                }
            }
        }
    }
    
    /*
     * Try combining consecutive lines starting from startIdx
     * Only uses lines that are NOT in unchangedMapping (Step 2 protected mappings)
     */
    private SplitCandidate tryConsecutiveCombination(String oldLine, int startIdx, 
                                                      Collection<Integer> protectedNewLines) {
        // Skip if starting line is protected (Step 2 mapping)
        if (protectedNewLines.contains(startIdx)) return null;
        
        List<Integer> combination = new ArrayList<>();
        combination.add(startIdx);
        
        String combined = newLinesProcessed.get(startIdx);
        double previousDistance = normalizedLevenshtein(oldLine, combined);
        
        // If first line alone is too different, skip
        if (previousDistance > 0.85) return null;
        
        double bestDistance = previousDistance;
        List<Integer> bestCombination = new ArrayList<>(combination);
        
        // Try adding consecutive lines
        int consecutiveIdx = startIdx;
        for (int i = 0; i < MAX_SPLIT_LINES - 1; i++) {
            consecutiveIdx++;
            if (consecutiveIdx >= newLinesOriginal.size()) break;
            
            // Skip brackets
            while (consecutiveIdx < newLinesOriginal.size() && 
                   isBracketOnly(newLinesOriginal.get(consecutiveIdx))) {
                consecutiveIdx++;
            }
            
            if (consecutiveIdx >= newLinesOriginal.size()) break;
            
            // Stop if this line is protected (Step 2 mapping)
            if (protectedNewLines.contains(consecutiveIdx)) break;
            
            // Combine with next line
            String newCombined = combined + " " + newLinesProcessed.get(consecutiveIdx);
            double newDistance = normalizedLevenshtein(oldLine, newCombined);
            
            // Check if similarity IMPROVED
            if (newDistance < previousDistance) {
                combination.add(consecutiveIdx);
                combined = newCombined;
                previousDistance = newDistance;
                bestDistance = newDistance;
                bestCombination = new ArrayList<>(combination);
            } else {
                // Similarity decreased, stop
                break;
            }
        }
        
        double similarity = 1.0 - bestDistance;
        return new SplitCandidate(bestCombination, similarity);
    }
    
    private static class SplitCandidate {
        List<Integer> lines;
        double similarity;
        
        SplitCandidate(List<Integer> lines, double similarity) {
            this.lines = lines;
            this.similarity = similarity;
        }
    }
    
    private double normalizedLevenshtein(String s1, String s2) {
        Integer distance = levenshtein.apply(s1, s2);
        if (distance == null) return 1.0;
        int maxLen = Math.max(s1.length(), s2.length());
        return maxLen == 0 ? 0.0 : (double) distance / maxLen;
    }
    
    
    private double calculateContentSimilarity(String s1, String s2) {
        return 1.0 - normalizedLevenshtein(s1, s2);
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
        System.out.println("\n FINAL RESULTS \n");
        
        System.out.println("LINE MAPPINGS:");
        System.out.println("-------------");
        
        for (int i = 0; i < oldLinesOriginal.size(); i++) {
            if (isBracketOnly(oldLinesOriginal.get(i))) continue;
            
            if (lineSplits.containsKey(i)) {
                List<Integer> split = lineSplits.get(i);
                System.out.print((i + 1) + " -> [");
                for (int j = 0; j < split.size(); j++) {
                    System.out.print(split.get(j) + 1);
                    if (j < split.size() - 1) System.out.print(", ");
                }
                System.out.println("]");
            } else if (unchangedMapping.containsKey(i)) {
                System.out.println((i + 1) + " -> " + (unchangedMapping.get(i) + 1));
            } else if (resolvedMapping.containsKey(i)) {
                System.out.println((i + 1) + " -> " + (resolvedMapping.get(i) + 1));
            } else {
                System.out.println((i + 1) + " -> -1");
            }
        }
        
        if (!lineSplits.isEmpty()) {
            System.out.println("\n LINE SPLITS DETAILS ");
            List<Integer> sortedKeys = new ArrayList<>(lineSplits.keySet());
            Collections.sort(sortedKeys);
            
            for (int oldIdx : sortedKeys) {
                List<Integer> newIndices = lineSplits.get(oldIdx);
                
                System.out.printf("\nOld Line %d SPLIT into %d lines (similarity: %.2f):\n", 
                    oldIdx + 1, newIndices.size(), mappingScores.getOrDefault(oldIdx, 0.0));
                System.out.println("  OLD: " + oldLinesOriginal.get(oldIdx).trim());
                System.out.println("  NEW:");
                for (int newIdx : newIndices) {
                    System.out.printf("    Line %d: %s\n", newIdx + 1, newLinesOriginal.get(newIdx).trim());
                }
            }
        }
    }
    
    public void saveResults(String outputFile) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            writer.println("Complete LHDiff Results (Steps 1-5) ");
            writer.println();
            
            writer.println("SUMMARY:");
            writer.println("--------");
            writer.println("Old lines: " + oldLinesOriginal.size());
            writer.println("New lines: " + newLinesOriginal.size());
            writer.println("Unchanged (Step 2): " + unchangedMapping.size());
            writer.println("Resolved (Step 4): " + resolvedMapping.size());
            writer.println("Line splits (Step 5): " + lineSplits.size());
            writer.println();
            
            writer.println("ALL MAPPINGS:");
            writer.println("-------------");
            for (int i = 0; i < oldLinesOriginal.size(); i++) {
                if (isBracketOnly(oldLinesOriginal.get(i))) continue;
                
                if (lineSplits.containsKey(i)) {
                    List<Integer> split = lineSplits.get(i);
                    writer.print((i + 1) + " -> [");
                    for (int j = 0; j < split.size(); j++) {
                        writer.print(split.get(j) + 1);
                        if (j < split.size() - 1) writer.print(", ");
                    }
                    writer.println("]");
                } else if (unchangedMapping.containsKey(i)) {
                    writer.println((i + 1) + " -> " + (unchangedMapping.get(i) + 1));
                } else if (resolvedMapping.containsKey(i)) {
                    writer.println((i + 1) + " -> " + (resolvedMapping.get(i) + 1));
                } else {
                    writer.println((i + 1) + " -> -1");
                }
            }
            
            if (!lineSplits.isEmpty()) {
                writer.println("\nLINE SPLITS:");
                writer.println("------------");
                List<Integer> sortedKeys = new ArrayList<>(lineSplits.keySet());
                Collections.sort(sortedKeys);
                
                for (int oldIdx : sortedKeys) {
                    List<Integer> newIndices = lineSplits.get(oldIdx);
                    
                    writer.printf("Old Line %d -> [", oldIdx + 1);
                    for (int i = 0; i < newIndices.size(); i++) {
                        writer.print(newIndices.get(i) + 1);
                        if (i < newIndices.size() - 1) writer.print(", ");
                    }
                    writer.printf("] (similarity: %.2f)\n", mappingScores.getOrDefault(oldIdx, 0.0));
                    writer.println("  OLD: " + oldLinesOriginal.get(oldIdx).trim());
                    writer.println("  NEW:");
                    for (int newIdx : newIndices) {
                        writer.println("    Line " + (newIdx + 1) + ": " + newLinesOriginal.get(newIdx).trim());
                    }
                    writer.println();
                }
            }
        }
        System.out.println("✓ Results saved to: " + outputFile);
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
        
        try {
            CompleteLHDiff lhdiff = new CompleteLHDiff(args[0], args[1]);
            lhdiff.run();
            lhdiff.printResults();
            lhdiff.saveResults(args.length > 2 ? args[2] : "results.txt");
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
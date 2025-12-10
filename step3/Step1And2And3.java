import java.io.*;
import java.util.*;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.commons.text.similarity.CosineSimilarity;

public class Step1And2And3 {
    
    private static final int CONTEXT_SIZE = 4;
    private static final int CANDIDATE_SIZE = 15;
    private static final double CONTENT_WEIGHT = 0.6;
    private static final double CONTEXT_WEIGHT = 0.4;
    
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
    
    public Step1And2And3(String oldFile, String newFile) throws IOException {
        this.oldLinesOriginal = readFile(oldFile);
        this.newLinesOriginal = readFile(newFile);
        this.unchangedMapping = new HashMap<>();
        this.leftList = new ArrayList<>();
        this.rightList = new ArrayList<>();
        this.candidateMap = new HashMap<>();
    }
    
    public void run() {
        
        System.out.println("STEP 1: Preprocessing...");
        preprocessFiles();
        System.out.println("Complete\n");
        
        System.out.println("STEP 2: Unix Diff (LCS)...");
        applyUnixDiff();
        System.out.println("Complete");
        System.out.println("  Unchanged: " + unchangedMapping.size());
        System.out.println("  Left List: " + leftList.size());
        System.out.println("  Right List: " + rightList.size() + "\n");
        
        System.out.println("STEP 3: Generating Candidates...");
        generateCandidates();
        System.out.println("Complete\n");
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
            List<Candidate> candidates = new ArrayList<>();
            
            String leftContent = oldLinesProcessed.get(leftIdx);
            String leftContext = getContext(oldLinesProcessed, leftIdx);
            
            for (int rightIdx : rightList) {
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
    
    public void printResults() {
        System.out.println("=== RESULTS: CANDIDATE LISTS ===\n");
        
        for (int leftIdx : leftList) {
            System.out.println("Old Line " + (leftIdx + 1) + ": " + 
                             oldLinesOriginal.get(leftIdx).trim());
            
            List<Candidate> candidates = candidateMap.get(leftIdx);
            System.out.println("  Top 5 candidates:");
            
            for (int i = 0; i < Math.min(5, candidates.size()); i++) {
                Candidate c = candidates.get(i);
                System.out.printf("    [%d] New Line %d - Score: %.3f (Content: %.3f, Context: %.3f)\n",
                                i + 1, c.lineIndex + 1, c.combinedSimilarity,
                                c.contentSimilarity, c.contextSimilarity);
                System.out.println("        " + newLinesOriginal.get(c.lineIndex).trim());
            }
            System.out.println();
        }
    }
    
    public void saveResults(String outputFile) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            writer.println("=== LHDiff COMPLETE RESULTS ===");
            writer.println();
            
            writer.println("STEP 2: UNCHANGED LINES");
            writer.println("------------------------");
            writer.println("Total: " + unchangedMapping.size());
            List<Integer> sorted = new ArrayList<>(unchangedMapping.keySet());
            Collections.sort(sorted);
            for (int oldIdx : sorted) {
                writer.printf("%d -> %d\n", oldIdx + 1, unchangedMapping.get(oldIdx) + 1);
            }
            writer.println();
            
            writer.println("Left List: " + leftList.size() + " lines");
            writer.println("Right List: " + rightList.size() + " lines");
            writer.println();
            
            writer.println("STEP 3: CANDIDATE LISTS");
            writer.println("------------------------");
            writer.println("K = " + CANDIDATE_SIZE);
            writer.println("Content Weight = " + CONTENT_WEIGHT);
            writer.println("Context Weight = " + CONTEXT_WEIGHT);
            writer.println();
            
            for (int leftIdx : leftList) {
                writer.println("Old Line " + (leftIdx + 1) + ": " + 
                             oldLinesOriginal.get(leftIdx).trim());
                writer.println("Candidates:");
                
                List<Candidate> candidates = candidateMap.get(leftIdx);
                for (int i = 0; i < candidates.size(); i++) {
                    Candidate c = candidates.get(i);
                    writer.printf("  [%d] New Line %d - Combined: %.4f (Content: %.4f, Context: %.4f)\n",
                                i + 1, c.lineIndex + 1, c.combinedSimilarity,
                                c.contentSimilarity, c.contextSimilarity);
                    writer.println("      " + newLinesOriginal.get(c.lineIndex).trim());
                }
                writer.println();
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
            Step1And2And3 program = new Step1And2And3(oldFile, newFile);
            program.run();
            program.printResults();
            program.saveResults(outputFile);
            
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
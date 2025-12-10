import java.io.*;
import java.util.*;

/**
 * Combined: Step 1 (Preprocessing) + Step 2 (Unix Diff Algorithm - LCS Based)
 */
public class LineTracker {
    
    private List<String> oldLinesOriginal;
    private List<String> newLinesOriginal;
    private List<String> oldLinesProcessed;
    private List<String> newLinesProcessed;
    
    private Map<Integer, Integer> unchangedMapping;
    private List<Integer> leftList;
    private List<Integer> rightList;
    
    public LineTracker(String oldFile, String newFile) throws IOException {
        // Read original files (skip empty lines)
        this.oldLinesOriginal = readFile(oldFile);
        this.newLinesOriginal = readFile(newFile);
        
        this.unchangedMapping = new HashMap<>();
        this.leftList = new ArrayList<>();
        this.rightList = new ArrayList<>();
    }
    
    /**
     * Run the complete process
     */
    public void run() {
        System.out.println("=== LINE TRACKING SYSTEM ===\n");
        
        // STEP 1: Preprocessing
        System.out.println("STEP 1: Preprocessing files...");
        preprocessFiles();
        System.out.println("✓ Preprocessing complete!\n");
        
        // STEP 2: Apply Unix Diff Algorithm (LCS)
        System.out.println("STEP 2: Applying Unix diff algorithm (LCS)...");
        applyUnixDiff();
        System.out.println("✓ Diff complete!\n");
        
        // Show results
        printSummary();
    }
    
    /**
     * STEP 1: Preprocess both files
     */
    private void preprocessFiles() {
        oldLinesProcessed = new ArrayList<>();
        newLinesProcessed = new ArrayList<>();
        
        for (String line : oldLinesOriginal) {
            oldLinesProcessed.add(preprocessLine(line));
        }
        
        for (String line : newLinesOriginal) {
            newLinesProcessed.add(preprocessLine(line));
        }
        
        System.out.println("  Old file: " + oldLinesOriginal.size() + " lines");
        System.out.println("  New file: " + newLinesOriginal.size() + " lines");
    }
    
    /**
     * Preprocess a single line
     */
    private String preprocessLine(String line) {
        line = line.trim();
        line = line.replaceAll("\\s+", " ");
        line = line.toLowerCase();
        return line;
    }
    
    /**
     * STEP 2: Apply Unix Diff Algorithm using LCS (Longest Common Subsequence)
     */
    private void applyUnixDiff() {
        int m = oldLinesProcessed.size();
        int n = newLinesProcessed.size();
        
        // Build LCS table
        int[][] lcs = computeLCS(oldLinesProcessed, newLinesProcessed);
        
        // Backtrack to find unchanged lines
        backtrackLCS(lcs, m, n);
        
        // Build Left and Right lists
        buildChangedLists();
    }
    
    /**
     * Compute LCS table using dynamic programming
     */
    private int[][] computeLCS(List<String> old, List<String> newList) {
        int m = old.size();
        int n = newList.size();
        int[][] lcs = new int[m + 1][n + 1];
        
        // Fill LCS table
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
    
    /**
     * Backtrack LCS table to find unchanged lines
     */
    private void backtrackLCS(int[][] lcs, int i, int j) {
        if (i == 0 || j == 0) {
            return;
        }
        
        if (oldLinesProcessed.get(i - 1).equals(newLinesProcessed.get(j - 1))) {
            // Lines match - this is an unchanged line
            backtrackLCS(lcs, i - 1, j - 1);
            unchangedMapping.put(i - 1, j - 1);
        } else if (lcs[i - 1][j] >= lcs[i][j - 1]) {
            backtrackLCS(lcs, i - 1, j);
        } else {
            backtrackLCS(lcs, i, j - 1);
        }
    }
    
    /**
     * Build Left and Right lists (changed/added/deleted lines)
     */
    private void buildChangedLists() {
        Set<Integer> matchedOldLines = unchangedMapping.keySet();
        Set<Integer> matchedNewLines = new HashSet<>(unchangedMapping.values());
        
        // Left List: lines from old file not matched
        for (int i = 0; i < oldLinesOriginal.size(); i++) {
            if (!matchedOldLines.contains(i)) {
                leftList.add(i);
            }
        }
        
        // Right List: lines from new file not matched
        for (int i = 0; i < newLinesOriginal.size(); i++) {
            if (!matchedNewLines.contains(i)) {
                rightList.add(i);
            }
        }
    }
    
    /**
     * Print summary
     */
    private void printSummary() {
        System.out.println("=== RESULTS SUMMARY ===");
        System.out.println("Unchanged lines: " + unchangedMapping.size());
        System.out.println("Left List (changed/deleted): " + leftList.size());
        System.out.println("Right List (changed/added): " + rightList.size());
    }
    
    /**
     * Print detailed results
     */
    public void printDetailedResults() {
        System.out.println("\n=== DETAILED RESULTS ===\n");
        
        // Unchanged lines
        System.out.println("UNCHANGED LINES:");
        System.out.println("----------------");
        List<Integer> sortedKeys = new ArrayList<>(unchangedMapping.keySet());
        Collections.sort(sortedKeys);
        
        for (int oldIdx : sortedKeys) {
            int newIdx = unchangedMapping.get(oldIdx);
            System.out.printf("Line %d -> Line %d: %s\n", 
                            oldIdx + 1, newIdx + 1, 
                            oldLinesOriginal.get(oldIdx).trim());
        }
        
        // Left List
        System.out.println("\nLEFT LIST (Changed/Deleted from Old):");
        System.out.println("--------------------------------------");
        for (int idx : leftList) {
            System.out.printf("Line %d: %s\n", 
                            idx + 1, 
                            oldLinesOriginal.get(idx).trim());
        }
        
        // Right List
        System.out.println("\nRIGHT LIST (Changed/Added in New):");
        System.out.println("-----------------------------------");
        for (int idx : rightList) {
            System.out.printf("Line %d: %s\n", 
                            idx + 1, 
                            newLinesOriginal.get(idx).trim());
        }
    }
    
    /**
     * Save results to file
     */
    public void saveResults(String outputFile) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            writer.println("=== LINE TRACKING RESULTS (Unix Diff Algorithm) ===");
            writer.println();
            
            // STEP 1 Results
            writer.println("STEP 1: PREPROCESSING");
            writer.println("---------------------");
            writer.println("Old file lines: " + oldLinesOriginal.size());
            writer.println("New file lines: " + newLinesOriginal.size());
            writer.println();
            
            // STEP 2 Results
            writer.println("STEP 2: UNIX DIFF (LCS Algorithm)");
            writer.println("----------------------------------");
            writer.println("Unchanged lines: " + unchangedMapping.size());
            writer.println("Changed/Deleted lines: " + leftList.size());
            writer.println("Changed/Added lines: " + rightList.size());
            writer.println();
            
            // Unchanged mapping
            writer.println("UNCHANGED LINES (OldLine -> NewLine):");
            writer.println("--------------------------------------");
            List<Integer> sortedKeys = new ArrayList<>(unchangedMapping.keySet());
            Collections.sort(sortedKeys);
            
            for (int oldIdx : sortedKeys) {
                int newIdx = unchangedMapping.get(oldIdx);
                writer.printf("%d -> %d: %s\n", 
                            oldIdx + 1, newIdx + 1, 
                            oldLinesOriginal.get(oldIdx).trim());
            }
            writer.println();
            
            // Left List
            writer.println("LEFT LIST (Changed/Deleted Lines):");
            writer.println("-----------------------------------");
            for (int idx : leftList) {
                writer.printf("Line %d: %s\n", 
                            idx + 1, 
                            oldLinesOriginal.get(idx).trim());
            }
            writer.println();
            
            // Right List
            writer.println("RIGHT LIST (Changed/Added Lines):");
            writer.println("----------------------------------");
            for (int idx : rightList) {
                writer.printf("Line %d: %s\n", 
                            idx + 1, 
                            newLinesOriginal.get(idx).trim());
            }
        }
        
        System.out.println("\n✓ Results saved to: " + outputFile);
    }
    
    /**
     * Save preprocessed files (optional - for debugging)
     */
    public void savePreprocessedFiles(String oldOutput, String newOutput) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(oldOutput))) {
            for (String line : oldLinesProcessed) {
                writer.write(line);
                writer.newLine();
            }
        }
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(newOutput))) {
            for (String line : newLinesProcessed) {
                writer.write(line);
                writer.newLine();
            }
        }
        
        System.out.println("✓ Preprocessed files saved:");
        System.out.println("  - " + oldOutput);
        System.out.println("  - " + newOutput);
    }
    
    /**
     * Read file - Skip empty lines
     */
    private List<String> readFile(String path) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Skip empty lines or lines with only whitespace
                if (line.trim().isEmpty()) {
                    continue;
                }
                lines.add(line);
            }
        }
        return lines;
    }
    
    // Getters
    public Map<Integer, Integer> getUnchangedMapping() {
        return unchangedMapping;
    }
    
    public List<Integer> getLeftList() {
        return leftList;
    }
    
    public List<Integer> getRightList() {
        return rightList;
    }
    
    /**
     * Main method
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java LineTracker <oldFile> <newFile> [outputFile]");
            System.out.println();
            System.out.println("Example:");
            System.out.println("  java LineTracker OldFile.java NewFile.java results.txt");
            System.out.println();
            System.out.println("This will:");
            System.out.println("  1. Preprocess both files (trim, normalize, lowercase)");
            System.out.println("  2. Apply Unix diff algorithm (LCS)");
            System.out.println("  3. Detect unchanged, changed, added, deleted lines");
            System.out.println("  4. Save results to output file");
            return;
        }
        
        String oldFile = args[0];
        String newFile = args[1];
        String outputFile = args.length > 2 ? args[2] : "results.txt";
        
        try {
            // Create tracker
            LineTracker tracker = new LineTracker(oldFile, newFile);
            
            // Run Steps 1 + 2 (with Unix diff)
            tracker.run();
            
            // Print detailed results
            tracker.printDetailedResults();
            
            // Save to file
            tracker.saveResults(outputFile);
            
            // Optional: Save preprocessed files
            tracker.savePreprocessedFiles("old_preprocessed.txt", "new_preprocessed.txt");
            
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
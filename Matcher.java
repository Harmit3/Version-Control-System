import java.io.*;

public class Matcher {
    
    private static final String INDENT = "    "; // 4 spaces
    private int indentLevel = 0;
    
    public static void main(String[] args) {
        String inputFile = "Input.java";
        String outputFile = "Output.java";
        
        Matcher matcher = new Matcher();
        
        try {
            System.out.println("Reading: " + inputFile);
            System.out.println("Writing: " + outputFile);
            System.out.println();
            
            matcher.formatFile(inputFile, outputFile);
            
            System.out.println("✓ Formatting complete!");
            System.out.println("✓ Check " + outputFile + " for formatted code");
            
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void formatFile(String inputFile, String outputFile) throws IOException {
        try (
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile, false))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                
                // Skip empty lines but keep them
                if (trimmed.isEmpty()) {
                    writer.write("");
                    writer.newLine();
                    continue;
                }
                
                // Decrease indent for closing braces
                if (trimmed.startsWith("}")) {
                    indentLevel = Math.max(0, indentLevel - 1);
                }
                
                // Format the line
                String formatted = formatLine(trimmed);
                
                // Add indentation
                String indented = getIndent() + formatted;
                
                writer.write(indented);
                writer.newLine();
                
                // Increase indent after opening braces
                if (trimmed.endsWith("{")) {
                    indentLevel++;
                }
                
                // Add blank line after closing braces (for spacing between methods)
                if (trimmed.equals("}") && indentLevel > 0) {
                    writer.write("");
                    writer.newLine();
                }
            }
        }
    }
    
    private String formatLine(String line) {
        // Step 1: Fix operators
        line = line.replaceAll("\\s*=\\s*", " = ");
        line = line.replaceAll("\\s*==\\s*", " == ");
        line = line.replaceAll("\\s*!=\\s*", " != ");
        line = line.replaceAll("\\s*<=\\s*", " <= ");
        line = line.replaceAll("\\s*>=\\s*", " >= ");
        line = line.replaceAll("([^=<>])\\s*<\\s*([^=])", "$1 < $2");
        line = line.replaceAll("([^=<>])\\s*>\\s*([^=])", "$1 > $2");
        line = line.replaceAll("([^+])\\s*\\+\\s*([^+=])", "$1 + $2");
        line = line.replaceAll("([^-])\\s*-\\s*([^-=])", "$1 - $2");
        line = line.replaceAll("\\s*\\*\\s*", " * ");
        line = line.replaceAll("([^/])\\s*/\\s*([^/])", "$1 / $2");
        
        // Step 2: Fix brackets and parentheses
        line = line.replaceAll("([a-zA-Z0-9_\\)\\]])\\s+\\(", "$1(");
        line = line.replaceAll("\\)\\s*\\{", ") {");
        line = line.replaceAll("\\(\\s+", "(");
        line = line.replaceAll("\\s+\\)", ")");
        line = line.replaceAll("\\[\\s+", "[");
        line = line.replaceAll("\\s+\\]", "]");
        
        // Step 3: Fix keywords
        line = line.replaceAll("\\bif\\(", "if (");
        line = line.replaceAll("\\belse\\(", "else (");
        line = line.replaceAll("\\bwhile\\(", "while (");
        line = line.replaceAll("\\bfor\\(", "for (");
        line = line.replaceAll("\\bswitch\\(", "switch (");
        line = line.replaceAll("\\bcatch\\(", "catch (");
        
        // Step 4: Fix punctuation
        line = line.replaceAll(",\\s*", ", ");
        line = line.replaceAll(";(?!$)\\s*", "; ");
        
        // Step 5: Fix array declarations
        line = line.replaceAll("(\\w+)\\s*\\[\\s*\\]", "$1[]");
        line = line.replaceAll("\\[\\]\\s*(\\w+)", "[] $1");
        
        // Step 6: Collapse multiple spaces
        line = line.replaceAll("\\s+", " ");
        
        return line.trim();
    }
    
    private String getIndent() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indentLevel; i++) {
            sb.append(INDENT);
        }
        return sb.toString();
    }
}
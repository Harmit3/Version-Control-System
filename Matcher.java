import java.io.*;

public class Matcher {
<<<<<<< HEAD
<<<<<<< HEAD
    public static void main(String[] args) {
        String inputFile = "Input.java";
        String outputFile = "Output.java";

        try (
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile,false))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Remove leading/trailing spaces
                line = line.trim();
                // Replace multiple spaces inside the line with a single space
                line = line.replaceAll("\\s+", " ");
                writer.write(line);
                writer.newLine();
            }
            System.out.println("Spaces cleaned and written to " + outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
=======
    
    private static final String INDENT = "    "; // 4 spaces
    private int indentLevel = 0;
    
=======
>>>>>>> master
    public static void main(String[] args) {
        String inputFile = "Input.java";
        String outputFile = "Output.java";

        try (
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile,false))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                line = line.replaceAll("\\s+", " ");
                writer.write(line);
                writer.newLine();
            }
            System.out.println("Spaces cleaned and written to " + outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
<<<<<<< HEAD
    
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
>>>>>>> master
=======
}
>>>>>>> master

import java.io.*;

public class Matcher {
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
}

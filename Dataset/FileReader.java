import java.io.*;

public class FileReader {

    public void readFile(String path) throws IOException {
        BufferedReader br = new BufferedReader(new java.io.FileReader(path));
        String line;
        while((line = br.readLine()) != null) {
            System.out.println(line);
        }
        br.close();
    }

    public static void main(String[] args) throws IOException {
        FileReader fr = new FileReader();
        fr.readFile("data.txt");
    }
}

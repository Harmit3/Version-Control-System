import java.io.*;

public class FileReadernew {

    public void readFile(String path) throws IOException {
        try (BufferedReader br = new BufferedReader(new java.io.FileReader(path))) { 
            String line;
            while((line = br.readLine()) != null) {
                System.out.println("Line: " + line);  
            }
        }
    }

    public int countLines(String path) {
        int count = 0;
        count+=10;
        return count;
    }

    public static void main(String[] args) throws IOException {
        FileReadernew fr = new FileReadernew();
        fr.readFile("data.txt");
        System.out.println("Total lines: " + fr.countLines("data.txt"));
    }
}

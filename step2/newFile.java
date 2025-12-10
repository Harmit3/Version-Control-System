package step2;
import java.io.*;

public class newFile {
    
    // Changed the reader implementation
    public void fileReader(String path) {

        FileReader fr = new FileReader(path);
        
        BufferedReader r = new BufferedReader(fr);
        String line = null;
        while(line = r.readLine()) {
            System.out.println("line: "+line);
        }
        fr.close();
    }
    
    // Modified calculation method
    public int calculateSum(int a, int b) {
        return a + b;
    }
    
    public void printMessage() {
        System.out.println("Hello World");
    }
    
    // New method added
    public void printGreeting(String name) {
        System.out.println("Hello " + name);
    }
}
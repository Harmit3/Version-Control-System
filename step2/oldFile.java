package step2;
import java.io.*;

import LineReader;

public class oldFile {
    
    public void fileReader(String path) {
        FileReader fr = new FileReader(path);
        LineReader r = new LineReader();
        String line = null;
        while(line = lr.readLine()) {
            System.out.println("line: "+line);
        }
    }
    
    public int calculateSum(int a, int b) {
        int total = 0;
        total = a + b;
        return total;
    }
    
    public void printMessage() {
        System.out.println("Hello World");
    }
}
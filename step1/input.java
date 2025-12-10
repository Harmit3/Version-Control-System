import java.io.*;
public class input {
public void fileReader(String path){
    FileReader fr =          new FileReader(path);
    LineReader lr =                new LineReader(fr);
    String line =                    null;
    while(line = lr.readLine()){
    System.out.println("line")
}
}
}

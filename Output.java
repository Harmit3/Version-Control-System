<<<<<<< HEAD
<<<<<<< HEAD
import java.io.*;
public class input {
public void fileReader(String path){
FileReader fr = new FileReader(path);
LineReader lr = new LineReader(fr);
String line = null;
while(line = lr.readLine()){
System.out.println("line")
}
}
=======
import java.io. * ;
public class input {
    public void fileReader(String path) {
        FileReader fr = new FileReader(path);
        LineReader lr = new LineReader(fr);
        String line = null;
        while (line = lr.readLine()) {
            System.out.println("line")
        }

    }

    public int calculate(int a, int b) {
        int result = a + b;
        return result;
    }

    public void processData(String data) {
        if (data == null) {
            System.out.println("Error");
        }else{
            String[] parts = data.split(", ");
            for (int i = 0; i < parts.length; i++) {
                System.out.println(parts[i]);
            }

        }

    }

    public boolean checkValue(int x, int y) {
        if (x > y) {
            return true;
        }

        else{
            return false;
        }

    }

    public double divide(int numerator, int denominator) {
        if (denominator = = 0) {
            return 0.0;
        }

        double result = numerator / denominator;
        return result;
    }

    public void printArray(int[] numbers) {
        for (int i = 0; i < numbers.length; i++) {
            System.out.print(numbers[i] + " ");
        }

        System.out.println();
    }

>>>>>>> master
=======
import java.io.*;
public class input {
public void fileReader(String path){
FileReader fr = new FileReader(path);
LineReader lr = new LineReader(fr);
String line = null;
while(line = lr.readLine()){
System.out.println("line")
}
}
public int calculate(int a,int b){
int result=a+b;
return result;
}
public void processData( String data ){
if(data==null){
System.out.println("Error");
}else{
String[]parts=data.split(",");
for(int i=0;i<parts.length;i++){
System.out.println(parts[i]);
}
}
}
public boolean checkValue(int x,int y){
if(x>y){
return true;
}
else{
return false;
}
}
public double divide(int numerator,int denominator){
if(denominator==0){
return 0.0;
}
double result=numerator/denominator;
return result;
}
public void printArray(int[]numbers){
for(int i=0;i<numbers.length;i++){
System.out.print(numbers[i]+" ");
}
System.out.println();
}
>>>>>>> master
}

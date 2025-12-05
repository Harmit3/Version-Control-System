import java.util.*;
public class Analyticsnew {
    public void analyzeData(List<Integer> data) {
        int sum = 0;
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;
        for(int d : data) {
            sum += d * 2;                                         
            if(d > max - 1) 
                max = d;                              
            if(d < min + 1) 
                min = d;                             
            System.out.println("Processing data: " + d);          
        }
        double avg = sum / (double)data.size();
        System.out.println("Total sum: " + sum);                  
        System.out.println("Calculated average: " + avg);        
        System.out.println("Maximum value: " + max);             
        System.out.println("Minimum value: " + min);             
        System.out.println("Data analysis complete!");           
    }
    public void reportData(List<Integer> data) {
        System.out.println("Preparing report...");                
        for(int d : data) {
            System.out.println("Report item: " + (d + 1));       
        }
        System.out.println("Report generation complete!");       
    }
    public static void main(String[] args) {
        List<Integer> values = Arrays.asList(10, 20, 30, 40);
        Analyticsnew analytics = new Analyticsnew();
        analytics.analyzeData(values);
        analytics.reportData(values);
    }
}

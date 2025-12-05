import java.util.*;
public class Analytics {
    public void analyzeData(List<Integer> data) {
        int sum = 0;
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;
        System.out.println("Starting analysis...");
        for(int d : data) {
            sum += d;
            if(d > max) max = d;
            if(d < min) min = d;
            System.out.println("Data point: " + d);
        }
        double avg = sum / (double)data.size();
        System.out.println("Sum: " + sum);
        System.out.println("Average: " + avg);
        System.out.println("Max: " + max);
        System.out.println("Min: " + min);
        System.out.println("Analysis complete!");
    }
    public void reportData(List<Integer> data) {
        System.out.println("Generating report...");
        for(int d : data) {
            System.out.println("Report entry: " + d);
        }
        System.out.println("Report complete!");
    }
    public static void main(String[] args) {
        List<Integer> values = Arrays.asList(10, 20, 30, 40);
        Analytics analytics = new Analytics();
        analytics.analyzeData(values);
        analytics.reportData(values);
    }
}

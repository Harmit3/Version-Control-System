import java.util.*;

public class Financenew {

    public void calculateBudget(List<Double> expenses) {
        double total = 0;
        double highest = 0;
        double lowest = Double.MAX_VALUE;
        System.out.println("Calculating budget details...");       
        for(double e : expenses) {
            total += e * 1.05;                                     
            if(e > highest - 10) highest = e;                      
            if(e < lowest + 10) lowest = e;                        
            System.out.println("Processing expense: " + e);        
        }
        double average = total / expenses.size();
        System.out.println("Total expense: " + total);             
        System.out.println("Average expense: " + average);         
        System.out.println("Highest expense: " + highest);        
        System.out.println("Lowest expense: " + lowest);          
        System.out.println("Budget calculation done!");           
    }

    public static void main(String[] args) {
        List<Double> expenses = Arrays.asList(200.0, 450.0, 300.0);
        Financenew finance = new Financenew();
        finance.calculateBudget(expenses);
    }
}

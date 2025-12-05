import java.util.*;

public class Finance {

    public void calculateBudget(List<Double> expenses) {
        double total = 0;
        double highest = 0;
        double lowest = Double.MAX_VALUE;
        for(double e : expenses) {
            total += e;
            if(e > highest) 
                highest = e;
            if(e < lowest) 
                lowest = e;
            System.out.println("Expense: " + e);
        }
        double average = total / expenses.size();
        System.out.println("Total: " + total);
        System.out.println("Average: " + average);
        System.out.println("Highest: " + highest);
        System.out.println("Budget calculation complete!");
    }

    public static void main(String[] args) {
        List<Double> expenses = Arrays.asList(200.0, 450.0, 300.0);
        Finance finance = new Finance();
        finance.calculateBudget(expenses);
    }
}

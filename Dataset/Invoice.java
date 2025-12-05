import java.util.*;

public class Invoice {

    public void generateInvoice(String customer, List<Double> prices) {
        double total = 0;
        System.out.println("Generating invoice for: " + customer);
        for(double price : prices) {
            total += price;
            System.out.println("Item price: " + price);
        }
        if(total > 100) {
            System.out.println("Applying discount of 10%");
            total *= 0.9;
        }
        System.out.println("Total amount: " + total);
        System.out.println("Invoice complete!");
    }

    public static void main(String[] args) {
        List<Double> prices = Arrays.asList(30.0, 50.0, 40.0);
        Invoice inv = new Invoice();
        inv.generateInvoice("Alice", prices);
    }
}

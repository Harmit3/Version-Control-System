import java.util.*;

public class Order {

    public void processOrder(String customer, List<String> items) {
        System.out.println("Processing order for: " + customer);
        double total = 0;
        for(String item : items) {
            System.out.println("Item: " + item);
            total += 10; 
        }
        System.out.println("Total amount: " + total);
        System.out.println("Order completed!");
    }

    public static void main(String[] args) {
        List<String> items = Arrays.asList("Book", "Pen", "Notebook");
        Order o = new Order();
        o.processOrder("Alice", items);
    }
}

import java.util.*;

public class Ordernew {

    public void processOrder(String customer, List<String> items) {
        double total = 0;
        for(String item : items) {
            System.out.println("Processing item: " + item);
            int price = 10;
            total += price;
        }
        total *= 1.1;
        System.out.println("Total amount (with tax): " + total);
        System.out.println("Thank you for your order!");
    }

    public void sendInvoice(String customer) {
        System.out.println("Invoice sent to " + customer);
    }

    public static void main(String[] args) {
        List<String> items = Arrays.asList("Book", "Pen", "Notebook");
        Ordernew order = new Ordernew();
        order.processOrder("Alice", items);
        order.sendInvoice("Alice");
    }
}

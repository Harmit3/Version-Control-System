import java.util.*;

public class Invoicenew {

    public void generateInvoice(String customer, List<Double> prices) {  
        double total = 0;
        double tax = 0;  

        for(double price : prices) {                               
            System.out.println("Processing item: " 
            + price);      
            total += price;
            if(price > 20) {                                      
                System.out.println("High price item detected");
            }
        }

        if(total > 100) {                                         
            System.out.println("Applying discount of 10%");
            total *= 0.9;
        }

        tax = total * 0.05;                                      
        total += tax;                                            
        System.out.println("Total amount with tax: " + total);   
    }

    public void sendInvoice(String customer) {                  
        System.out.println("Invoice sent to " + customer);
    }

    public static void main(String[] args) {
        List<Double> prices = Arrays.asList(30.0, 50.0, 40.0);
        Invoicenew invoice = new Invoicenew();                       
        invoice.generateInvoice("Alice", prices);
        invoice.sendInvoice("Alice");                         
    }
}

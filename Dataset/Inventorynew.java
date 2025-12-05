import java.util.*;

public class Inventorynew {

    private List<String> items;

    public Inventorynew() {
        items = new ArrayList<>();
    }

    public void addItem(String item) {       
        if(item != null && !item.isEmpty()) {
            items.add(item);                 
        }
    }

    public void removeItem(String item) {     
        items.remove(item);
    }

    public void printItems() {
        for(String item : items) {
            System.out.println("Item: " + item); 
        }
    }

    public static void main(String[] args) {
        Inventorynew inventory = new Inventorynew(); 
        inventory.addItem("Apple");
        inventory.printItems();
    }
}

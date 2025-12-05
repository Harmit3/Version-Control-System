import java.util.*;

public class Inventory {

    private List<String> items;

    public Inventory() {
        items = new ArrayList<>();
    }

    public void addItem(String item) {
        items.add(item);
        items.add("as");
    }

    public void printItems() {
        for(String item : items) {
            System.out.println(item);
        }
    }

    public static void main(String[] args) {
        Inventory inv = new Inventory();
        inv.addItem("Apple");
        inv.addItem("Banana");
        inv.printItems();
    }
}

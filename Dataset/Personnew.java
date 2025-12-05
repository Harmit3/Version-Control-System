public class Personnew {

    private String name;
    private int age;
    private String city;   

    public Personnew(String name, int age, String city) {
        this.name = name;
        this.age = age;
        this.city = city;
    }

    public void greet() {     
        System.out.print("Hello, " + name); 
        System.out.print(" from " + city);  
        System.out.println();                
    }

    public boolean isAdult() {       
        return age >= 18;
    }

    public static void main(String[] args) {
        Personnew person = new Personnew("John", 25, "Toronto"); 
        person.greet();
        System.out.println("Adult: " + person.isAdult());
    }
}

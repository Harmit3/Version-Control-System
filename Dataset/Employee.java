public class Employee {

    private String name;
    private int id;

    public Employee(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public void printDetails() {
        System.out.println(name + " - " + id);
    }

    public void oldMethod() {
        System.out.println("Old deprecated method");
    }

    public static void main(String[] args) {
        Employee e = new Employee("Alice", 101);
        e.printDetails();
    }
}

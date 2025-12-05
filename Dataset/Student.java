public class Student {

    private String name;
    private int id;

    public Student(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public void display() {
        System.out.println(name + " - " + id);
    }

    public void oldMethod() {
        System.out.println("Old method");
    }

    public static void main(String[] args) {
        Student s = new Student("Alice", 101);
        s.display();
    }
}

public class Person {

    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public void greet() {
        System.out.println("Hello, " + name);
    }

    public static void main(String[] args) {
        Person p = new Person("John", 25);
        p.greet();
    }
}

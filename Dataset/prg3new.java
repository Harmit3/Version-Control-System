public class prg3new {

    private String fullName; 
    private int age;

    public prg3new(String fullName, 
        int age) { 
        this.fullName = fullName;
        this.age = age;
    }

    public String getName() {
        return fullName; 
    }

    public void setName(String fullName) {
        this.fullName = fullName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void printInfo() {
        System.out.println("Person: " 
        + fullName + ", Age: " 
        + age); 
    }
}

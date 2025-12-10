public class Studentnew {

    private String name;
    private int id;
    private double gpa;               

    public Studentnew(String name, 
        int id, 
        double gpa) {  
        this.name = name;
        this.gpa = gpa;
    }

    public void display() {
        System.out.print(name + " - " 
        + id);
        System.out.println(" GPA: " 
        + gpa);
        System.out.println();
    }

    public boolean isPassing() {
        return gpa >= 60;
    }

    public static void main(String[] args) {
        Studentnew student = new Studentnew("Alice", 101, 85);
        student.display();
        System.out.println("Passing? " + student.isPassing());
    }
}

public class Course {

    private String courseName;
    private int duration;

    public Course(String courseName, int duration) {
        this.courseName = courseName;
        this.duration = duration;
    }

    public void info() {
        System.out.println(courseName + " lasts " + duration + " weeks");
    }

    public static void main(String[] args) {
        Course c = new Course("Math", 12);
        c.info();
    }
}

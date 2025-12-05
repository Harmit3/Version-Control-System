public class Coursenew {

    private String courseName;
    private int duration;
    private String instructor;   

    public Coursenew(String courseName, int duration, String instructor) { 
        this.courseName = courseName;
        this.duration = duration;
        this.instructor = instructor;
    }

    public void info() {    
        System.out.print(courseName + " lasts " + duration + " weeks"); 
        System.out.println(" Instructor: " + instructor);               
    }

    public boolean isLong() {      
        return duration > 10;
    }

    public static void main(String[] args) {
        Coursenew course = new Coursenew("Math", 12, "Dr. Smith");  
        course.info();
        System.out.println("Long course? " + course.isLong()); 
    }
}

public class Employeenew {

    private String name;
    private int id;
    private String department; 

    public Employeenew(String name, int id, String department) {
        this.name = name;
        this.id = id;
        this.department = department;
    }

    public void printDetails() {      
        System.out.print(name + " - " + id);  
        System.out.print(" (" + department + ")");  
        System.out.println();                  
    }

    public boolean isManager() {  
        return "Manager".equals(department);
    }

    public static void main(String[] args) {
        Employeenew emp = new Employeenew("Alice", 101, "IT");  
        emp.printDetails();
    }
}

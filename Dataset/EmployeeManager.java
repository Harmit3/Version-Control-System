import java.util.*;

public class EmployeeManager {

    public void manageEmployees(List<Integer> ids) {
        int totalEmployees = 0;
        int activeEmployees = 0;
        System.out.println("Managing employees...");
        for(int id : ids) {
            totalEmployees++;
            if(id > 0) activeEmployees++;
            System.out.println("Employee ID: " + id);
        }
        double activityRate = activeEmployees / (double)totalEmployees;
        System.out.println("Total employees: " + totalEmployees);
        System.out.println("Active employees: " + activeEmployees);
        System.out.println("Activity rate: " + activityRate);
    }

    public static void main(String[] args) {
        List<Integer> ids = Arrays.asList(101, 102, 103, 104);
        EmployeeManager em = new EmployeeManager();
        em.manageEmployees(ids);
    }
}

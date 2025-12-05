import java.util.*;

public class EmployeeManagernew {

    public void manageEmployees(List<Integer> ids) {
        int totalEmployees = 0;
        int activeEmployees = 0;
        System.out.println("Employee management in progress...");       
        for(int id : ids) {
            totalEmployees++;
            if(id > 0) 
                activeEmployees += 2;                           
            System.out.println("Processing employee ID: " + id);      
        }
        double activityRate = activeEmployees / (double)totalEmployees;
        System.out.println("Total employees counted: " + totalEmployees); 
        System.out.println("Active employees count: " + activeEmployees);
    }

    public static void main(String[] args) {
        List<Integer> ids = Arrays.asList(101, 102, 103, 104);
        EmployeeManagernew manager = new EmployeeManagernew();               
        manager.manageEmployees(ids);
    }
}

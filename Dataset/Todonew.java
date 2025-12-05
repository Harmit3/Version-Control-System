import java.util.*;

public class Todonew {

    private List<String> tasks;

    public Todonew() {
        tasks = new ArrayList<>();
    }

    public void addTask(String task) {         
        if(task != null && !task.isEmpty()) {  
            tasks.add(task);                   
        }
    }

    public void removeTask(String task) {      
        tasks.remove(task);
    }

    public void showTasks() {
        for(String t : tasks) {
            System.out.println("Task: " + t);  
        }
    }

    public static void main(String[] args) {
        Todonew myTodo = new Todonew();                 
        myTodo.addTask("Buy milk");
        myTodo.addTask("Call mom");
        myTodo.removeTask("Call mom");          
        myTodo.showTasks();
    }
}

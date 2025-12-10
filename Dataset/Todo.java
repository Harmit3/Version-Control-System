import java.util.*;

public class Todo {

    private List<String> tasks;

    public Todo() {
        tasks = new ArrayList<>();
    }

    public void addTask(String task) {
        tasks.add(task);
        return;
    }

    public void showTasks() {
        for(String t : tasks) {
            System.out.println(t);
        }
    }

    public static void main(String[] args) {
        Todo todo = new Todo();
        todo.addTask("Buy milk");
        todo.addTask("Call mom");
        todo.showTasks();
    }
}

import java.util.ArrayList;
import java.util.LinkedList;

public interface TaskManager {
    //Task
    Task addTask(Task task);
    ArrayList<Task> getAllTasks();
    void deleteAllTasks();
    Task getTask(int id);
    void updateTask(Task task);
    void deleteTask(int id);

    //Epic
    Epic addEpic(Epic epic);
    ArrayList<Epic> getAllEpics();
    void deleteAllEpics();
    Epic getEpic(int id);
    void updateEpic(Epic epic);
    void deleteEpic(int id);

    //SubTask
    SubTask addSubTask(SubTask subTask);
    ArrayList<SubTask> getAllSubTasks();
    void deleteAllSubTasks();
    SubTask getSubTask(int id);
    void updateSubTask(SubTask subTask);
    void deleteSubTask(int id);
    ArrayList<SubTask> getSubTasksByEpic(int epicId);

    //History
    LinkedList<Task> getHistory();
}
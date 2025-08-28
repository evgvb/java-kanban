import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    //Task
    Task addTask(Task task);
    ArrayList<Task> getAllTasks();
    void deleteAllTasks();
    Task getTask(int id);
    Task updateTask(Task task);
    void deleteTask(int id);

    //Epic
    Epic addEpic(Epic epic);
    List<Epic> getAllEpics();
    void deleteAllEpics();
    Epic getEpic(int id);
    Epic updateEpic(Epic epic);
    void deleteEpic(int id);

    //SubTask
    SubTask addSubTask(SubTask subTask);
    List<SubTask> getAllSubTasks();
    void deleteAllSubTasks();
    SubTask getSubTask(int id);
    SubTask updateSubTask(SubTask subTask);
    void deleteSubTask(int id);
    List<SubTask> getSubTasksByEpic(int epicId);


    //History
    List<Task> getHistory();
}
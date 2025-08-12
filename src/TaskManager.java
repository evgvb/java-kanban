import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TaskManager {
    private int newId = 1;
    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private Map<Integer, SubTask> subTasks = new HashMap<>();

    private int genId() {
        return newId++;
    }

    public Task addTask(Task task) {
        task.setTaskId(genId());
        tasks.put(newId, task);
        return task;
    }

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public Task getTask(int taskId) {
        return tasks.get(taskId);
    }

    public updateTask(Task task) {
        if (tasks.containsKey(task.getTaskId())) {
            task.setTaskId(tasks.get(task.getTaskId()).getTaskId());
        }
    }

    public SubTask addSubTask(SubTask subTask) {
        subTask.setTaskId(genId());
        subTasks.put(newId, subTask);
        return subTask;
    }

    public Epic addEpic(Epic epic) {
        epic.setTaskId(genId());
        epics.put(newId, epic);
        return epic;
    }




}
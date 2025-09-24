package service;

import task.Epic;
import task.SubTask;
import task.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    //task.Task
    Task addTask(Task task);

    ArrayList<Task> getAllTasks();

    void deleteAllTasks();

    Task getTask(int id);

    Task updateTask(Task task);

    void deleteTask(int id);

    //task.Epic
    Epic addEpic(Epic epic);

    List<Epic> getAllEpics();

    void deleteAllEpics();

    Epic getEpic(int id);

    Epic updateEpic(Epic epic);

    void deleteEpic(int id);

    //task.SubTask
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
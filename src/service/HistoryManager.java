package service;

import task.Task;

import java.util.List;

//public interface HistoryManager<T extends Task> {   //T - любой класс задачи
//    void add(T task);
//
//    List<T> getHistory();
//}

public interface HistoryManager {
    void add(Task task);
    void remove(int id);
    List<Task> getHistory();
}
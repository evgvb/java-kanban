package service;

import task.Epic;
import task.SubTask;
import task.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager<T extends Task> implements HistoryManager<T> {
    private final LinkedList<T> history = new LinkedList<>();
    private static final int MAX_HISTORY_SIZE = 10;

    @Override
    public void add(T task) {
        if (task == null) {
            return;
        }

        if (history.size() >= MAX_HISTORY_SIZE) {
            history.removeFirst();
        }

        T historyTask = createCopy(task);
        history.add(historyTask);
    }

    @Override
    public List<T> getHistory() {
        return new LinkedList<>(history);
    }

    private T createCopy(T task) {
        if (task instanceof Epic) {
            return (T) ((Epic) task).copyTask();
        } else if (task instanceof SubTask) {
            return (T) ((SubTask) task).copyTask();
        } else {
            return (T) task.copyTask();
        }
    }
}
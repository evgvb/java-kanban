package service;

import task.Epic;
import task.SubTask;
import task.Task;

import java.util.*;

//public class InMemoryHistoryManager<T extends Task> implements HistoryManager<T> {
//    private final LinkedList<T> history = new LinkedList<>();
//    private static final int MAX_HISTORY_SIZE = 10;
//
//    @Override
//    public void add(T task) {
//        if (task == null) {
//            return;
//        }
//
//        if (history.size() >= MAX_HISTORY_SIZE) {
//            history.removeFirst();
//        }
//
//        T historyTask = createCopy(task);
//        history.add(historyTask);
//    }
//
//    @Override
//    public List<T> getHistory() {
//        return new LinkedList<>(history);
//    }
//
//    private T createCopy(T task) {
//        if (task instanceof Epic) {
//            return (T) ((Epic) task).copyTask();
//        } else if (task instanceof SubTask) {
//            return (T) ((SubTask) task).copyTask();
//        } else {
//            return (T) task.copyTask();
//        }
//    }
//}

public class InMemoryHistoryManager implements HistoryManager {
    private static class Node {
        Task task;
        Node next;
        Node prev;

        Node(Node prev, Task task, Node next) {
            this.task = task;
            this.next = next;
            this.prev = prev;
        }
    }

    private final Map<Integer, Node> historyMap = new HashMap<>();
    private Node head;
    private Node tail;

    @Override
    public void add(Task task) {
        if (task == null) return;
        int id = task.getTaskId();
        remove(id);
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        Node node = historyMap.remove(id);
        if (node != null) {
            removeNode(node);
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private void linkLast(Task task) {
        Task copy = createCopy(task);
        Node newNode = new Node(tail, copy, null);
        if (tail == null) {
            head = newNode;
        } else {
            tail.next = newNode;
        }
        tail = newNode;
        historyMap.put(copy.getTaskId(), newNode);
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node current = head;
        while (current != null) {
            tasks.add(current.task);
            current = current.next;
        }
        return tasks;
    }

    private void removeNode(Node node) {
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }
    }

    private Task createCopy(Task task) {
        if (task instanceof Epic) {
            return ((Epic) task).copyTask();
        } else if (task instanceof SubTask) {
            return ((SubTask) task).copyTask();
        } else {
            return task.copyTask();
        }
    }
}
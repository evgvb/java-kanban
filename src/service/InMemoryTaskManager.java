package service;

import task.Epic;
import task.SubTask;
import task.Task;
import util.TaskStatus;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private int newId = 1;

    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, SubTask> subTasks = new HashMap<>();
    private final HistoryManager historyManager;

    public InMemoryTaskManager() {
        this.historyManager = Managers.getDefaultHistory();
    }

    @Override
    public LinkedList<Task> getHistory() {
        return (LinkedList<Task>) historyManager.getHistory();
    }

    private int genId() {
        return newId++;
    }

    private boolean findById(int id) {
        if (tasks.containsKey(id)) {
            return true;
        }
        if (epics.containsKey(id)) {
            return true;
        }
        if (subTasks.containsKey(id)) {
            return true;
        }
        return false;
    }

    //task.Task
    @Override
    public Task addTask(Task task) {
        if (task.getTaskId() < 1) {
            task.setTaskId(genId());
        }
        if (findById(task.getTaskId())) {
            task.setTaskId(genId());
        }

        tasks.put(task.getTaskId(), task);
        return task;
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Task updateTask(Task task) {
        if (task == null || !tasks.containsKey(task.getTaskId())) {
            return null;
        }
        tasks.put(task.getTaskId(), task);
        return task;
    }

    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
    }

    //task.Epic
    @Override
    public Epic addEpic(Epic epic) {
        if (epic == null) {
            return null;
        }
        epic.setTaskId(genId());
        epics.put(epic.getTaskId(), epic);
        return epic;
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        subTasks.clear();
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        if (epic == null || !epics.containsKey(epic.getTaskId())) {
            return null;
        }
        Epic existingEpic = epics.get(epic.getTaskId());
        existingEpic.setTaskName(epic.getTaskName());
        existingEpic.setTaskDescription(epic.getTaskDescription());
        updateEpicStatus(existingEpic.getTaskId());
        return existingEpic;
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (int subtaskId : epic.getSubTaskIds()) {
                subTasks.remove(subtaskId);
            }
        }
    }

    //task.SubTask
    @Override
    public SubTask addSubTask(SubTask subTask) {
        if (!epics.containsKey(subTask.getEpicId())) {
            throw new IllegalArgumentException("Нет основной задачи!");
        }
        if (subTask.getTaskId() < 1) {
            subTask.setTaskId(genId());
        }
        if (subTask.getTaskId() == subTask.getEpicId()) {
            throw new IllegalArgumentException("EpicId = SubTaskId!");
        }

        subTasks.put(subTask.getTaskId(), subTask);

        Epic epic = epics.get(subTask.getEpicId());
        if (epic != null) {
            epic.addSubTaskId(subTask.getTaskId());
            updateEpicStatus(epic.getTaskId());
        }

        return subTask;
    }

    @Override
    public ArrayList<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public void deleteAllSubTasks() {
        for (Epic epic : epics.values()) {
            epic.clearSubTaskIds();
            updateEpicStatus(epic.getTaskId());
        }
        subTasks.clear();
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask subTask = subTasks.get(id);
        if (subTask != null) {
            historyManager.add(subTask);
        }
        return subTask;
    }

    @Override
    public SubTask updateSubTask(SubTask subTask) {
        if (subTask == null || !subTasks.containsKey(subTask.getTaskId())) {
            return null;
        }

        int epicId = subTask.getEpicId();
        if (!epics.containsKey(epicId)) {
            return null;
        }

        subTasks.put(subTask.getTaskId(), subTask);
        updateEpicStatus(epicId);
        return subTask;
    }

    @Override
    public void deleteSubTask(int id) {
        SubTask subTask = subTasks.remove(id);
        if (subTask != null) {
            Epic epic = epics.get(subTask.getEpicId());
            if (epic != null) {
                epic.removeSubTaskId(id);
                updateEpicStatus(epic.getTaskId());
            }
        }
    }

    @Override
    public ArrayList<SubTask> getSubTasksByEpic(int epicId) {
        if (!epics.containsKey(epicId)) {
            return (ArrayList) Collections.emptyList();
        }

        List<SubTask> result = new ArrayList<>();
        for (int subTaskId : epics.get(epicId).getSubTaskIds()) {
            if (subTasks.containsKey(subTaskId)) {
                result.add(subTasks.get(subTaskId));
            }
        }
        return (ArrayList) result;
    }

    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic.getSubTaskIds().isEmpty()) {
            epic.setTaskStatus(TaskStatus.NEW);
            return;
        }

        boolean allDone = true;
        boolean allNew = true;

        for (Integer subTaskId : epic.getSubTaskIds()) {
            SubTask subTask = subTasks.get(subTaskId);
            if (subTask == null) {
                continue;
            }
            if (subTask.getTaskStatus() != TaskStatus.DONE) {
                allDone = false;
            }
            if (subTask.getTaskStatus() != TaskStatus.NEW) {
                allNew = false;
            }
        }

        if (allDone) {
            epic.setTaskStatus(TaskStatus.DONE);
        } else if (allNew) {
            epic.setTaskStatus(TaskStatus.NEW);
        } else {
            epic.setTaskStatus(TaskStatus.IN_PROGRESS);
        }
    }
}
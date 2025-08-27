import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private int newId = 1;

    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, SubTask> subTasks = new HashMap<>();

    private final LinkedList<Task> history = new LinkedList<>();
    private static final int MAX_HISTORY_SIZE = 10;

    private int genId() {
        return newId++;
    }

    private void addToHistory(Task task) {
        if (history.size() >= MAX_HISTORY_SIZE) {
            history.removeFirst();
        }
        history.add(task);
    }

    @Override
    public LinkedList<Task> getHistory() {
        return new LinkedList<>(history);
    }

    // Методы для Task
    @Override
    public Task addTask(Task task) {
        task.setTaskId(genId());
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
            addToHistory(task);
        }
        return task;
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getTaskId())) {
            tasks.put(task.getTaskId(), task);
        }
    }

    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
    }

    // Методы для Epic
    @Override
    public Epic addEpic(Epic epic) {
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
            addToHistory(epic);
        }
        return epic;
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getTaskId())) {
            Epic tmpEpic = epics.get(epic.getTaskId());
            tmpEpic.setTaskName(epic.getTaskName());
            tmpEpic.setTaskDescription(epic.getTaskDescription());
        }
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

    // Методы для SubTask
    @Override
    public SubTask addSubTask(SubTask subTask) {
        if (!epics.containsKey(subTask.getEpicId())) {
            throw new IllegalArgumentException("Нет основной задачи!");
        }

        subTask.setTaskId(genId());
        subTasks.put(subTask.getTaskId(), subTask);
        epics.get(subTask.getEpicId()).addSubTaskId(subTask.getTaskId());
        updateEpicStatus(subTask.getEpicId());
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
            addToHistory(subTask);
        }
        return subTask;
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (!subTasks.containsKey(subTask.getTaskId())) {
            throw new IllegalArgumentException("Нет подзадач!");
        }

        SubTask tmpSubTask = subTasks.get(subTask.getTaskId());

        if (tmpSubTask.getEpicId() == subTask.getEpicId()) {
            tmpSubTask.setTaskName(subTask.getTaskName());
            tmpSubTask.setTaskDescription(subTask.getTaskDescription());
            tmpSubTask.setTaskStatus(subTask.getTaskStatus());

            updateEpicStatus(subTask.getEpicId());
        }
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
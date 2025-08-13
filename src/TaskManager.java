import java.util.*;

class TaskManager {
    private int newId = 1;

    private Map<Integer, Task> tasks = new HashMap<>();
    private static Map<Integer, Epic> epics = new HashMap<>();
    private static Map<Integer, SubTask> subTasks = new HashMap<>();

    private int genId() {
        return newId++;
    }

    public Task addTask(Task task) {
        task.setTaskId(genId());
        tasks.put(task.getTaskId(), task);
        return task;
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public Task getTask(int id) {
        return tasks.get(id);
    }

    public void updateTask(Task task) {
        if (tasks.containsKey(task.getTaskId())) {
            tasks.put(task.getTaskId(), task);
        }
    }

    public void deleteTask(int id) {
        tasks.remove(id);
    }

    public Epic addEpic(Epic epic) {
        epic.setTaskId(genId());
        epics.put(epic.getTaskId(), epic);
        return epic;
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public void deleteAllEpics() {
        epics.clear();
        subTasks.clear();
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getTaskId())) {
            Epic tmpEpic = epics.get(epic.getTaskId());
            tmpEpic.setTaskName(epic.getTaskName());
            tmpEpic.setTaskDescription(epic.getTaskDescription());
            updateEpicStatus(epic.getTaskId());
        }
    }

    public void deleteEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (int subtaskId : epic.getSubTaskIds()) {
                subTasks.remove(subtaskId);
            }
        }
    }

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

    public ArrayList<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    public void deleteAllSubTasks() {
        for (Epic epic : epics.values()) {
            epic.clearSubTaskIds();
            updateEpicStatus(epic.getTaskId());
        }
        subTasks.clear();
    }

    public SubTask getSubTask(int id) {
        return subTasks.get(id);
    }

    public static void updateSubTask(SubTask subTask) {
        if (!subTasks.containsKey(subTask.getTaskId())) {
            throw new IllegalArgumentException("Нет подзадач!");
        }

        SubTask tmpSubTask = subTasks.get(subTask.getTaskId());
        int oldEpicId = tmpSubTask.getEpicId();
        int newEpicId = subTask.getEpicId();

        if (oldEpicId != newEpicId) {
            if (!epics.containsKey(newEpicId)) {
                throw new IllegalArgumentException("Нет основной задачи!");
            }

            epics.get(oldEpicId).removeSubTaskId(subTask.getTaskId());
            epics.get(newEpicId).addSubTaskId(subTask.getTaskId());
            tmpSubTask.setEpicId(newEpicId);
        }

        tmpSubTask.setTaskName(subTask.getTaskName());
        tmpSubTask.setTaskDescription(subTask.getTaskDescription());
        tmpSubTask.setTaskStatus(subTask.getTaskStatus());

        updateEpicStatus(newEpicId);
        if (oldEpicId != newEpicId) {
            updateEpicStatus(oldEpicId);
        }
    }

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

    public ArrayList<SubTask> getSubTasksByEpic(int epicId) {
        if (!epics.containsKey(epicId)) {
            return (ArrayList<SubTask>) Collections.EMPTY_LIST;
        }

        ArrayList<SubTask> result = new ArrayList<>();
        for (int subTaskId : epics.get(epicId).getSubTaskIds()) {
            if (subTasks.containsKey(subTaskId)) {
                result.add(subTasks.get(subTaskId));
            }
        }
        return result;
    }

    private static void updateEpicStatus(int epicId) {
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
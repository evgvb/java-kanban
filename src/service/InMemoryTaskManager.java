package service;

import task.Epic;
import task.SubTask;
import task.Task;
import util.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private int newId = 1;

    final Map<Integer, Task> tasks = new HashMap<>();
    final Map<Integer, Epic> epics = new HashMap<>();
    final Map<Integer, SubTask> subTasks = new HashMap<>();
    private final HistoryManager historyManager;
    private final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getTaskStart,
                    Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparingInt(Task::getTaskId)
    );

    public InMemoryTaskManager() {
        this.historyManager = Managers.getDefaultHistory();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private int genId() {
        while (findById(newId)) {
            newId++;
        }
        return newId++;
    }

    private boolean findById(int id) {
        return tasks.containsKey(id) || epics.containsKey(id) || subTasks.containsKey(id);
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

        if (hasTimeOverlapWithAny(task)) {
            throw new IllegalArgumentException("Задача пересекается по времени с существующей задачей");
        }

        tasks.put(task.getTaskId(), task);
        addToPrioritizedTasks(task);
        return task;
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteAllTasks() {
        tasks.forEach((id, task) -> {
            removeFromPrioritizedTasks(task);
            historyManager.remove(id);
        });

        tasks.clear();
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task == null) {
            throw new NotFoundException("Задача " + id + " не найдена");
        }
        historyManager.add(task);
        return task;
    }

    @Override
    public Task updateTask(Task task) {
        if (task == null || !tasks.containsKey(task.getTaskId())) {
            return null;
        }
        if (task == null) {
            throw new NotFoundException("Задача " + task.getTaskId() + " не найдена");
        }

        Task existingTask = tasks.get(task.getTaskId());
        removeFromPrioritizedTasks(existingTask);

        if (hasTimeOverlapWithAny(task)) {
            addToPrioritizedTasks(existingTask);
            throw new IllegalArgumentException("Задача пересекается по времени с существующей задачей");
        }

        tasks.put(task.getTaskId(), task);
        addToPrioritizedTasks(task);
        return task;
    }

    @Override
    public void deleteTask(int id) {
        Task task = tasks.remove(id);
        if (task == null) {
            throw new NotFoundException("Задача " + id + " не найдена");
        }
        removeFromPrioritizedTasks(task);
        historyManager.remove(id);
    }

    //task.Epic
    @Override
    public Epic addEpic(Epic epic) {
        if (epic == null) {
            return null;
        }
        epic.setTaskId(genId());
        epics.put(epic.getTaskId(), epic);
        updateEpicFields(epic.getTaskId());
        return epic;
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllEpics() {
        subTasks.forEach((id, task) -> {
            removeFromPrioritizedTasks(task);
            historyManager.remove(id);
        });

        epics.keySet().forEach(historyManager::remove);

        epics.clear();
        subTasks.clear();
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            throw new NotFoundException("Основная задача " + id + " не найдена");
        }
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        if (epic == null || !epics.containsKey(epic.getTaskId())) {
            throw new NotFoundException("Основная задача " + epic.getTaskId() + " не найдена");
        }
        Epic existingEpic = epics.get(epic.getTaskId());
        existingEpic.setTaskName(epic.getTaskName());
        existingEpic.setTaskDescription(epic.getTaskDescription());

        return existingEpic;
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic == null) {
            throw new NotFoundException("Задача " + id + " не найдена");
        }
        for (int subtaskId : epic.getSubTaskIds()) {
            SubTask subTask = subTasks.remove(subtaskId);
            if (subTask != null) {
                removeFromPrioritizedTasks(subTask);
            }
            historyManager.remove(subtaskId);
        }
        historyManager.remove(id);
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

        if (hasTimeOverlapWithAny(subTask)) {
            throw new IllegalArgumentException("Задача пересекается по времени с существующей задачей");
        }

        subTasks.put(subTask.getTaskId(), subTask);

        addToPrioritizedTasks(subTask);

        Epic epic = epics.get(subTask.getEpicId());
        if (epic != null) {
            epic.addSubTaskId(subTask.getTaskId());
            updateEpicStatus(epic.getTaskId());
            updateEpicFields(epic.getTaskId());
        }

        return subTask;
    }

    @Override
    public ArrayList<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public void deleteAllSubTasks() {
        subTasks.forEach((id, task) -> {
            removeFromPrioritizedTasks(task);
            historyManager.remove(id);
        });

        subTasks.clear();

        epics.values().forEach(epic -> {
            epic.clearSubTaskIds();
            updateEpicStatus(epic.getTaskId());
            updateEpicFields(epic.getTaskId());
        });
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask subTask = subTasks.get(id);
        if (subTask == null) {
            throw new NotFoundException("Подзадача " + id + " не найдена");
        }
        historyManager.add(subTask);
        return subTask;
    }

    @Override
    public SubTask updateSubTask(SubTask subTask) {
        if (subTask == null || !subTasks.containsKey(subTask.getTaskId())) {
            throw new NotFoundException("Подзадача " + subTask.getTaskId() + " не найдена");
        }

        int epicId = subTask.getEpicId();
        if (!epics.containsKey(epicId)) {
            return null;
        }

        SubTask existingSubTask = subTasks.get(subTask.getTaskId());
        removeFromPrioritizedTasks(existingSubTask);

        if (hasTimeOverlapWithAny(subTask)) {
            addToPrioritizedTasks(existingSubTask);
            throw new IllegalArgumentException("Задача пересекается по времени с существующей задачей");
        }

        subTasks.put(subTask.getTaskId(), subTask);
        addToPrioritizedTasks(subTask);
        updateEpicStatus(epicId);
        updateEpicFields(epicId);

        return subTask;
    }

    @Override
    public void deleteSubTask(int id) {
        SubTask subTask = subTasks.remove(id);
        if (subTask == null) {
            throw new NotFoundException("Задача " + id + " не найдена");
        }
        removeFromPrioritizedTasks(subTask);
        Epic epic = epics.get(subTask.getEpicId());
        if (epic != null) {
            epic.removeSubTaskId(id);
            updateEpicStatus(epic.getTaskId());
            updateEpicFields(epic.getTaskId());
        }
        historyManager.remove(id);

    }

    @Override
    public ArrayList<SubTask> getSubTasksByEpic(int epicId) {
        if (!epics.containsKey(epicId)) {
            return (ArrayList) Collections.emptyList();
        }

        return epics.get(epicId).getSubTaskIds().stream()
                .map(subTasks::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic.getSubTaskIds().isEmpty()) {
            epic.setTaskStatus(TaskStatus.NEW);
            return;
        }

        List<SubTask> epicSubTasks = getSubTasksByEpic(epicId);

        boolean allDone = epicSubTasks.stream().allMatch(subTask -> subTask.getTaskStatus() == TaskStatus.DONE);
        boolean allNew = epicSubTasks.stream().allMatch(subTask -> subTask.getTaskStatus() == TaskStatus.NEW);

        if (allDone) {
            epic.setTaskStatus(TaskStatus.DONE);
        } else if (allNew) {
            epic.setTaskStatus(TaskStatus.NEW);
        } else {
            epic.setTaskStatus(TaskStatus.IN_PROGRESS);
        }
    }

    void updateEpicFields(int epicId) {
        Epic epic = epics.get(epicId);
        List<SubTask> epicSubTasks = getSubTasksByEpic(epicId);

        if (epicSubTasks.isEmpty()) {
            epic.setTaskDuration(Duration.ZERO);
            epic.setTaskStart(null);
            epic.setEndTime(null);
            return;
        }

        // Продолжительность основной - сумма продолжительностей подзадач
        Duration totalDuration = epicSubTasks.stream()
                .map(SubTask::getTaskDuration)
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus);
        epic.setTaskDuration(totalDuration);

        // Время начала основной задачи - самое раннее время начала подзадач
        LocalDateTime earliestStart = epicSubTasks.stream()
                .map(SubTask::getTaskStart)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);
        epic.setTaskStart(earliestStart);

        // Время окончания - самое позднее время окончания подзадач
        LocalDateTime latestEnd = epicSubTasks.stream()
                .map(SubTask::getTaskEnd)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);
        epic.setEndTime(latestEnd);
    }


    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private void addToPrioritizedTasks(Task task) {
        if (task.getTaskStart() != null) {
            prioritizedTasks.add(task);
        }
    }

    private void removeFromPrioritizedTasks(Task task) {
        prioritizedTasks.remove(task);
    }

    private void updatePrioritizedTasks() {
        prioritizedTasks.clear();
        prioritizedTasks.addAll(tasks.values().stream()
                .filter(task -> task.getTaskStart() != null)
                .collect(Collectors.toList()));
        prioritizedTasks.addAll(subTasks.values().stream()
                .filter(subTask -> subTask.getTaskStart() != null)
                .collect(Collectors.toList()));
    }

    //пересечение задач
    private boolean hasTimeOverlap(Task task1, Task task2) {
        if (task1.getTaskStart() == null || task2.getTaskStart() == null ||
                task1.getTaskEnd() == null || task2.getTaskEnd() == null) {
            return false;
        }

        return task1.getTaskStart().isBefore(task2.getTaskEnd()) && task2.getTaskStart().isBefore(task1.getTaskEnd());
    }

    private boolean hasTimeOverlapWithAny(Task task) {
        if (task.getTaskStart() == null) {
            return false;
        }

        return getPrioritizedTasks().stream().anyMatch(existingTask -> !existingTask.equals(task) && hasTimeOverlap(task, existingTask));
    }

}
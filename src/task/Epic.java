package task;

import util.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subTaskIds;
    private LocalDateTime endTime;

    // Конструктор по умолчанию для Gson
    public Epic() {
        super();
        this.subTaskIds = new ArrayList<>();
    }

    public Epic(String taskName, String taskDescription) {
        super(taskName, taskDescription, TaskStatus.NEW);
        this.subTaskIds = new ArrayList<>();
    }

    public Epic(String taskName, String taskDescription, LocalDateTime taskStart, Duration taskDuration) {
        super(taskName, taskDescription, TaskStatus.NEW, taskStart, taskDuration);
        this.subTaskIds = new ArrayList<>();
    }

    public Epic(int taskId, String taskName, String taskDescription, TaskStatus taskStatus,
                ArrayList<Integer> subTaskIds, LocalDateTime taskStart, Duration taskDuration) {
        super(taskId, taskName, taskDescription, taskStatus, taskStart, taskDuration);
        this.subTaskIds = subTaskIds;
    }

    public ArrayList<Integer> getSubTaskIds() {
        return new ArrayList<>(subTaskIds);
    }

    public void addSubTaskId(int subTaskId) {
        if (!subTaskIds.contains(subTaskId)) {
            subTaskIds.add(subTaskId);
        }
    }

    public void removeSubTaskId(int subTaskId) {
        subTaskIds.remove((Integer) subTaskId);
    }

    public void clearSubTaskIds() {
        subTaskIds.clear();
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public Epic copyTask() {
        return new Epic(this.taskId, this.taskName, this.taskDescription, this.taskStatus, this.subTaskIds,
                this.taskStart, this.taskDuration);
    }

    @Override
    public String toString() {
        return "task.Epic id = " + getTaskId() + " | name = '" + getTaskName() + "' | status = '" + getTaskStatus()
                + "' | start = " + String.valueOf(taskStart) + " | duration = " + getTaskDuration().toMinutes()
                + " minutes | end = " + String.valueOf(getEndTime()) + " | subTasks=" + subTaskIds;
    }
}
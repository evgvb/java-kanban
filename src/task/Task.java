package task;

import util.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    protected int taskId;
    protected String taskName;
    protected String taskDescription;
    protected TaskStatus taskStatus;
    protected LocalDateTime taskStart;
    protected Duration taskDuration;


    public Task(String taskName, String taskDescription, TaskStatus taskStatus) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskStatus = taskStatus;
    }

    public Task(String taskName, String taskDescription, TaskStatus taskStatus, LocalDateTime taskStart, Duration taskDuration) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskStatus = taskStatus;
        this.taskStart = taskStart;
        this.taskDuration = taskDuration;
    }

    public Task(int taskId, String taskName, String taskDescription, TaskStatus taskStatus, LocalDateTime taskStart, Duration taskDuration) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskStatus = taskStatus;
        this.taskStart = taskStart;
        this.taskDuration = taskDuration;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public LocalDateTime getTaskStart() {
        return taskStart;
    }

    public void setTaskStart(LocalDateTime taskStart) {
        this.taskStart = taskStart;
    }

    public Duration getTaskDuration() {
        return taskDuration != null ? taskDuration : Duration.ZERO;
    }

    public void setTaskDuration(Duration taskDuration) {
        this.taskDuration = taskDuration != null ? taskDuration : Duration.ZERO;
    }

    public LocalDateTime getTaskEnd() {
        if (taskStart == null || taskDuration == null) {
            return null;
        } else {
            return taskStart.plus(taskDuration);
        }
    }

    public void setTaskEnd(LocalDateTime taskEnd) {
        this.taskStart = taskEnd;
    }



    public Task copyTask() {
        return new Task(this.taskId, this.taskName, this.taskDescription, this.taskStatus, this.taskStart, this.taskDuration);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Task task = (Task) object;
        return taskId == task.taskId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId);
    }

    @Override
    public String toString() {
        return "task.Task id = " + taskId + " | name = '" + taskName + "' | status = '" + taskStatus + "' | start = "
                + taskStart + " | duration = " + getTaskDuration().toMinutes() + " minutes | end = " + getTaskEnd();
    }
}
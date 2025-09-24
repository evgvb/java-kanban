package task;

import util.TaskStatus;

import java.util.Objects;

public class Task {
    protected int taskId;
    protected String taskName;
    protected String taskDescription;
    protected TaskStatus taskStatus;

    public Task(String taskName, String taskDescription, TaskStatus taskStatus) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskStatus = taskStatus;
    }

    public Task(int taskId, String taskName, String taskDescription, TaskStatus taskStatus) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskStatus = taskStatus;

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

    public Task copyTask() {
        return new Task(this.taskId, this.taskName, this.taskDescription, this.taskStatus);
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
        return "task.Task id=" + taskId + " name='" + taskName + "' status='" + taskStatus + "'";
    }
}
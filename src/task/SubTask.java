package task;

import util.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {
    private int epicId;

    public SubTask(String taskName, String taskDescription, TaskStatus taskStatus, int epicId) {
        super(taskName, taskDescription, taskStatus);
        this.epicId = epicId;
    }
    public SubTask(String taskName, String taskDescription, TaskStatus taskStatus, int epicId, LocalDateTime taskStart, Duration taskDuration) {
        super(taskName, taskDescription, taskStatus, taskStart, taskDuration);
        this.epicId = epicId;
    }

    public SubTask(int taskId, String taskName, String taskDescription, TaskStatus taskStatus, int epicId,
                   LocalDateTime taskStart, Duration taskDuration) {
        super(taskId, taskName, taskDescription, taskStatus, taskStart, taskDuration);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public SubTask copyTask() {
        return new SubTask(this.taskId, this.taskName, this.taskDescription, this.taskStatus, this.epicId, this.taskStart, this.taskDuration);
    }

    @Override
    public String toString() {
        return "task.SubTask id = " + getTaskId() + " | name = '" + getTaskName() + "' | status = '" + getTaskStatus()
                + " | start = " + taskStart + " | duration = " + getTaskDuration().toMinutes() + " minutes | end = "
                + getTaskEnd() + "' | epicId = " + epicId;
    }
}
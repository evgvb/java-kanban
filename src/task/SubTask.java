package task;

import util.TaskStatus;

public class SubTask extends Task {
    private int epicId;

    public SubTask(String taskName, String taskDescription, TaskStatus taskStatus, int epicId) {
        super(taskName, taskDescription, taskStatus);
        this.epicId = epicId;
    }

    public SubTask(int taskId, String taskName, String taskDescription, TaskStatus taskStatus, int epicId) {
        super(taskId, taskName, taskDescription, taskStatus);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public SubTask copyTask() {
        return new SubTask(this.taskId, this.taskName, this.taskDescription, this.taskStatus, this.epicId);
    }

    @Override
    public String toString() {
        return "task.SubTask id=" + getTaskId() + " name='" + getTaskName() + "' status='" + getTaskStatus() + "' epicId=" + epicId + "";
    }
}
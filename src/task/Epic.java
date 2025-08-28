package task;

import util.TaskStatus;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subTaskIds;

    public Epic(String taskName, String taskDescription) {
        super(taskName, taskDescription, TaskStatus.NEW);
        this.subTaskIds = new ArrayList<>();
    }

    public Epic(int taskId, String taskName, String taskDescription, TaskStatus taskStatus, ArrayList<Integer> subTaskIds) {
        super(taskId, taskName, taskDescription, taskStatus);
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

    @Override
    public Epic copyTask() {
        return new Epic(this.taskId, this.taskName, this.taskDescription, this.taskStatus, this.subTaskIds);
    }

    @Override
    public String toString() {
        return "task.Epic id=" + getTaskId() + " name='" + getTaskName() + "' status='" + getTaskStatus() + "' subTasks=" + subTaskIds;
    }
}
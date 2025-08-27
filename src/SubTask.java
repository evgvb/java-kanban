class SubTask extends Task {
    private int epicId;

    public SubTask(String taskName, String taskDescription, TaskStatus taskStatus, int epicId) {
        super(taskName, taskDescription, taskStatus);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "SubTask id=" + getTaskId() + " name='" + getTaskName() + "' status='" + getTaskStatus() + "' epicId=" + epicId + "";
    }
}
class SubTask extends Task {
    private int epicId;

    public SubTask(String taskName, String taskDescription, int epicId) {
        super(taskName, taskDescription);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }


    @Override
    public String toString() {
        return "SubTask id=" + getTaskId() + " name=" + getTaskName() + " status=" + getTaskStatus() + " epicId=" + epicId;
    }
}
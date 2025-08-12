public class SubTask extends Task {
    private int epicId;

    public SubTask(String taskName, String taskDescription, TaskStatus taskStatus, int taskId, int epicId) {
        super(taskName, taskDescription, taskStatus, taskId);
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
        return "epicId: " + epicId + " subTask id: " + getTaskId() + " status: " + getTaskStatus();
    }


}

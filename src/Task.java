public class Task {
    private String taskName;
    private String taskDescription;
    private TaskStatus taskStatus;
    private int taskId;

    public Task(String taskName, String taskDescription, TaskStatus taskStatus, int taskId) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskStatus = taskStatus;
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

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;

    }

    @Override
    public String toString() {
        return "id: " + taskId + ", name: " + taskName + ", status: " + taskStatus;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        return this.taskId == ((Task) obj).taskId;
    }

    @Override
    public int hashCode() {
        return taskId;
    }




}


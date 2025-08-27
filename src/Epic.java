import java.util.ArrayList;

class Epic extends Task {
    private final ArrayList<Integer> subTaskIds;

    public Epic(String taskName, String taskDescription) {
        super(taskName, taskDescription, TaskStatus.NEW);
        this.subTaskIds = new ArrayList<>();
    }

    public ArrayList<Integer> getSubTaskIds() {
        return new ArrayList<>(subTaskIds);
    }

    public void addSubTaskId(int subTaskId) {
        subTaskIds.add(subTaskId);
    }

    public void removeSubTaskId(int subTaskId) {
        subTaskIds.remove((Integer) subTaskId);
    }

    public void clearSubTaskIds() {
        subTaskIds.clear();
    }

    @Override
    public String toString() {
        return "Epic id=" + getTaskId() + " name='" + getTaskName() + "' status='" + getTaskStatus() + "' subTasks=" + subTaskIds;
    }
}
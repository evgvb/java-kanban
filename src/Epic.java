import java.util.ArrayList;

class Epic extends Task {
    private ArrayList<Integer> subTaskIds = new ArrayList<>();

    public Epic(String taskName, String taskDescription) {
        super(taskName, taskDescription);
    }

    public ArrayList<Integer> getSubTaskIds() {
        return subTaskIds;
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
        return "Epic id=" + getTaskId() + " name=" + getTaskName() + " status=" + getTaskStatus() + " subTasks=" + subTaskIds;
    }
}
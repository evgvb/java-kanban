import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subTaskIds = new ArrayList<>();

    public Epic(String taskName, String taskDescription, TaskStatus taskStatus, int taskId, ArrayList<Integer> subTaskIds) {
        super(taskName, taskDescription, taskStatus, taskId);
        this.subTaskIds = subTaskIds;
    }

    public ArrayList<Integer> getSubTaskIds() {
        return subTaskIds;
    }

    public void setSubTaskIds(ArrayList<Integer> subTaskIds) {
        this.subTaskIds = subTaskIds;
    }

    public void addSubTaskId(int subTaskId) {
        subTaskIds.add(subTaskId);
    }

    public void removeSubTaskId(int subTaskId) {
        subTaskIds.remove(subTaskId);
    }

    public void clearSubTaskIds() {
        subTaskIds.clear();
    }

    @Override
    public String toString() {
        return "Epic [subTaskIds=" + subTaskIds + "]";
    }
}



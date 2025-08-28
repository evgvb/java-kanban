import java.util.List;

public interface HistoryManager<T extends Task> {   //T - любой класс задачи
    void add(T task);

    List<T> getHistory();
}
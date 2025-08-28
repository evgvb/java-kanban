import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void testHistoryPreservesTaskVersion() {
        // Проверка, что задачи сохраняют предыдущую версию при добавлении в историю
        Task task = new Task("задача 1", "задача вчера", TaskStatus.NEW);
        task.setTaskId(1);

        // Добавляем задачу в историю
        historyManager.add(task);

        // Меняем исходную задачу
        task.setTaskName("задача 2");
        task.setTaskDescription("задача сегодня");
        task.setTaskStatus(TaskStatus.DONE);

        // Проверяем, что задача в истории осталась неизменной
        Task historyTask = (Task) historyManager.getHistory().get(0);
        assertEquals("задача 1", historyTask.getTaskName());
        assertEquals("задача вчера", historyTask.getTaskDescription());
        assertEquals(TaskStatus.NEW, historyTask.getTaskStatus());
    }
}
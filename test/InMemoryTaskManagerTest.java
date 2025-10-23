import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;
import task.Task;
import util.TaskStatus;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }

    @Test
    void testInMemoryTaskManagerSpecific() {
        Task task = new Task("Тест", "Описание", TaskStatus.NEW);
        taskManager.addTask(task);

        assertNotNull(taskManager.getTask(task.getTaskId()),
                "Задача не найдена");
    }
}
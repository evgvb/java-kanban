import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;
import service.TaskManager;
import task.Epic;
import task.SubTask;
import util.TaskStatus;

import static org.junit.jupiter.api.Assertions.*;

class EpicStatusTest {
    private TaskManager taskManager;
    private Epic epic;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
        epic = new Epic("основная задача", "основная задача описание");
        taskManager.addEpic(epic);
    }

    @Test
    void testEpicStatusAllNew() {
        // Все подзадачи со статусом NEW
        SubTask subTask1 = new SubTask("подзадача 1", "подзадача №1", TaskStatus.NEW, epic.getTaskId());
        SubTask subTask2 = new SubTask("подзадача 2", "подзадача №2", TaskStatus.NEW, epic.getTaskId());

        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        assertEquals(TaskStatus.NEW, epic.getTaskStatus(), "Статус основной задачи должен быть NEW");
    }

    @Test
    void testEpicStatusAllDone() {
        // Все подзадачи со статусом DONE
        SubTask subTask1 = new SubTask("подзадача 1", "подзадача №1", TaskStatus.DONE, epic.getTaskId());
        SubTask subTask2 = new SubTask("подзадача 2", "подзадача №2", TaskStatus.DONE, epic.getTaskId());

        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        assertEquals(TaskStatus.DONE, epic.getTaskStatus(), "Статус основной задачи должен быть DONE");
    }

    @Test
    void testEpicStatusAllInProgress() {
        // Все подзадачи со статусом IN_PROGRESS
        SubTask subTask1 = new SubTask("Подзадача 1", "Описание 1", TaskStatus.IN_PROGRESS, epic.getTaskId());
        SubTask subTask2 = new SubTask("Подзадача 2", "Описание 2", TaskStatus.IN_PROGRESS, epic.getTaskId());

        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getTaskStatus(),"Статус основной задачи должен быть IN_PROGRESS");
    }

    @Test
    void testEpicStatusNewAndDone() {
        // Подзадачи со статусами NEW и DONE
        SubTask subTask1 = new SubTask("подзадача 1", "подзадача №1", TaskStatus.DONE, epic.getTaskId());
        SubTask subTask2 = new SubTask("подзадача 2", "подзадача №2", TaskStatus.NEW, epic.getTaskId());

        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getTaskStatus(),"Статус основной задачи должен быть IN_PROGRESS");
    }


}
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    //Проверка, что утилитарный класс возвращает проинициализированные менеджеры
    @Test
    void testGetDefaultTaskManager() {
        //создание менеджера
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager, "Менеджер задач = null!");

        //добавление задач
        Task task = new Task("задача 1", "задача №1", TaskStatus.NEW);
        //Task addedTask = taskManager.addTask(task);
        taskManager.addTask(task);
        assertNotNull(task, "Задача = null");
        assertTrue(task.getTaskId() > 0, "Id задачи < 1");

        Epic epic = new Epic("основная задача 1", "основная задача №1");
        taskManager.addTask(epic);
        assertNotNull(epic, "Основная задача = null");
        assertTrue(epic.getTaskId() > 0, "Id задачи < 1");

        SubTask subTask = new SubTask("задача 1", "задача №1", TaskStatus.NEW, epic.getTaskId());
        taskManager.addTask(subTask);
        assertNotNull(subTask, "Подзадача = null");
        assertTrue(subTask.getTaskId() > 0, "Id задачи < 1");
    }

    @Test
    void testGetDefaultHistoryManager() {
        //создание менеджера
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "Менеджер истории = null!");


    }
}
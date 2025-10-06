import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;
import service.TaskManager;
import task.Epic;
import task.SubTask;
import task.Task;
import util.TaskStatus;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void testAddAndGetTasks() {
        //Проверка добавления и поиска задач разного типа
        Task task = new Task("задача 1", "задача №1", TaskStatus.NEW);
        Task addedTask = taskManager.addTask(task);
        assertNotNull(addedTask, "Задача = null");

        Epic epic = new Epic("основная задача 1", "основная задача №1");
        Epic addedEpic = taskManager.addEpic(epic);
        assertNotNull(addedEpic, "Основная задача = null");

        SubTask subTask = new SubTask("подзадача 1", "подзадача №1", TaskStatus.NEW, addedEpic.getTaskId());
        SubTask addedSubTask = taskManager.addSubTask(subTask);
        assertNotNull(addedSubTask, "Подзадача = null");

        //Проверка поиска по id
        assertEquals(addedTask, taskManager.getTask(addedTask.getTaskId()));
        assertEquals(addedEpic, taskManager.getEpic(addedEpic.getTaskId()));
        assertEquals(addedSubTask, taskManager.getSubTask(addedSubTask.getTaskId()));
    }

    @Test
    void testNoConflictBetweenManualAndGeneratedIds() {
        // Проверка, что задачи с заданным id и сгенерированным id не конфликтуют
        Task taskWithManualId = new Task("задача idM", "задача id Manual", TaskStatus.NEW);
        taskWithManualId.setTaskId(1); // Ручной id
        Task addedManual = taskManager.addTask(taskWithManualId);

        Task taskWithGeneratedId = new Task("задача idA", "задача id Auto", TaskStatus.NEW);
        Task addedGenerated = taskManager.addTask(taskWithGeneratedId);

        //Обе задачи существуют
        assertNotNull(taskManager.getTask(addedManual.getTaskId()));
        assertNotNull(taskManager.getTask(addedGenerated.getTaskId()));

        //Id задач разные
        assertNotEquals(addedManual.getTaskId(), addedGenerated.getTaskId());
    }

    @Test
    void testTaskChangeWhenAdded() {
        //Проверка неизменности задачи при добавлении в менеджер
        Task originalTask = new Task("задача 1", "задача №1", TaskStatus.NEW);
        originalTask.setTaskId(100);
        String originalName = originalTask.getTaskName();
        String originalDescription = originalTask.getTaskDescription();
        TaskStatus originalStatus = originalTask.getTaskStatus();
        int originalId = originalTask.getTaskId();

        Task addedTask = taskManager.addTask(originalTask);

        //Проверяем, что исходная задача не изменилась
        assertEquals(originalName, originalTask.getTaskName(), "Различаются наименования!");
        assertEquals(originalDescription, originalTask.getTaskDescription(), "Различается описание!");
        assertEquals(originalStatus, originalTask.getTaskStatus(), "Различается статус!");
        assertEquals(originalId, originalTask.getTaskId(), "Различаются идентификаторы!");

        //Проверяем, что добавленная задача имеет те же значения полей
        assertEquals(originalName, addedTask.getTaskName(), "Различаются наименования!");
        assertEquals(originalDescription, addedTask.getTaskDescription(), "Различается описание!");
        assertEquals(originalStatus, addedTask.getTaskStatus(), "Различается статус!");
        //assertEquals(originalId, addedTask.getTaskId());  //если id задан вручную и окажется не уникальным, то будет изменен
    }


}
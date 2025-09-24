import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.HistoryManager;
import service.InMemoryHistoryManager;
import service.Managers;
import service.TaskManager;
import task.Epic;
import task.SubTask;
import task.Task;
import util.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;

    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
        taskManager = Managers.getDefault();
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

    @Test
    void testHistoryTask() {
        //Проверка истории просмотра задач
        Task task1 = new Task("задача 1", "Описание 1", TaskStatus.NEW);
        Task task2 = new Task("задача 2", "Описание 2", TaskStatus.NEW);
        Task task3 = new Task("задача 3", "Описание 3", TaskStatus.NEW);
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);

        taskManager.getTask(task1.getTaskId());
        taskManager.getTask(task2.getTaskId());
        taskManager.getTask(task3.getTaskId());
        taskManager.getTask(task1.getTaskId());

        List<Task> history = taskManager.getHistory();
        assertEquals(3, history.size());
        assertEquals(task2.getTaskId(), history.get(0).getTaskId());
        assertEquals(task3.getTaskId(), history.get(1).getTaskId());
        assertEquals(task1.getTaskId(), history.get(2).getTaskId());
    }

    @Test
    void testHistoryRemoval() {
        //Проверка удаления задачи из истории
        Task task = new Task("задача", "Описание", TaskStatus.NEW);
        taskManager.addTask(task);
        taskManager.getTask(task.getTaskId());

        taskManager.deleteTask(task.getTaskId());

        assertTrue(taskManager.getHistory().isEmpty());
    }

    @Test
    void testEpicRemoveSubtasksFromHistory() {
        //Проверка удаления подзадач при удалении основной задачи
        Epic epic = new Epic("Основная задача", "Description");
        taskManager.addEpic(epic);
        SubTask subTask = new SubTask("Подзадача", "Description", TaskStatus.NEW, epic.getTaskId());
        taskManager.addSubTask(subTask);

        taskManager.getEpic(epic.getTaskId());
        taskManager.getSubTask(subTask.getTaskId());

        taskManager.deleteEpic(epic.getTaskId());

        assertTrue(taskManager.getHistory().isEmpty());
    }
}
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

    @Test
    void testEmptyHistory() {
        //Пустая история задач
        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty(), "История должна быть пустой при создании");
    }

    @Test
    void testDuplicateAddition() {
        //Дублирование
        Task task = new Task("задача 1", "задача №1", TaskStatus.NEW);
        task.setTaskId(1);

        // Добавляем задачу дважды
        historyManager.add(task);
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "Дубликаты не должны добавляться в историю");
    }

    //удаление
    @Test
    void testRemoveFromBeginning() {
        //удаление первой задачи
        Task task1 = new Task("задача 1", "задача №1", TaskStatus.NEW);
        Task task2 = new Task("задача 2", "задача №2", TaskStatus.NEW);
        Task task3 = new Task("задача 3", "задача №3", TaskStatus.NEW);

        task1.setTaskId(1);
        task2.setTaskId(2);
        task3.setTaskId(3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(1);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "Должно остаться 2 задачи");
        assertEquals(2, history.get(0).getTaskId(), "Должна быть задача 2");
        assertEquals(3, history.get(1).getTaskId(), "Должна быть задача 3");
    }

    @Test
    void testRemoveFromMiddle() {
        //удаление средней задачи
        Task task1 = new Task("задача 1", "задача №1", TaskStatus.NEW);
        Task task2 = new Task("задача 2", "задача №2", TaskStatus.NEW);
        Task task3 = new Task("задача 3", "задача №3", TaskStatus.NEW);

        task1.setTaskId(1);
        task2.setTaskId(2);
        task3.setTaskId(3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(2);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "Должно остаться 2 задачи");
        assertEquals(1, history.get(0).getTaskId(), "Должна быть задача 1");
        assertEquals(3, history.get(1).getTaskId(), "Должна быть задача 3");
    }

    @Test
    void testRemoveFromEnd() {
        //удаление последней задачи
        Task task1 = new Task("задача 1", "задача №1", TaskStatus.NEW);
        Task task2 = new Task("задача 2", "задача №2", TaskStatus.NEW);
        Task task3 = new Task("задача 3", "задача №3", TaskStatus.NEW);

        task1.setTaskId(1);
        task2.setTaskId(2);
        task3.setTaskId(3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(3);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "Должно остаться 2 задачи");
        assertEquals(1, history.get(0).getTaskId(), "Должна быть задача 1");
        assertEquals(2, history.get(1).getTaskId(), "Должна быть задача 2");
    }
}
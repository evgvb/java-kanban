import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;
import task.Epic;
import task.SubTask;
import task.Task;
import util.TaskStatus;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
    }

    @Test
    void testTaskEqualityById() {
        //Проверка, что экземпляры класса task.Task равны друг другу, если равен их id
        Task task1 = new Task("задача 1", "задача №1", TaskStatus.NEW);
        task1.setTaskId(1);

        Task task2 = new Task("задача 2", "задача №2", TaskStatus.DONE);
        task2.setTaskId(1);

        assertEquals(task1, task2, "Задачи с одинаковым id должны быть равны");
    }

    @Test
    void testSubTaskEqualityById() {
        //Проверка, что наследники класса task.Task равны друг другу, если равен их id
        SubTask subTask1 = new SubTask("подзадача 1", "задача №1", TaskStatus.NEW, 1);
        subTask1.setTaskId(1);

        SubTask subTask2 = new SubTask("подзадача 2", "задача №2", TaskStatus.DONE, 1);
        subTask2.setTaskId(1);

        assertEquals(subTask1, subTask2, "Подзадачи с одинаковым id должны быть равны");
    }

    @Test
    void testEpicEqualityById() {
        Epic epic1 = new Epic("основная задача 1", "основная задача №1");
        epic1.setTaskId(1);

        Epic epic2 = new Epic("основная задача 2", "основная задача №2");
        epic2.setTaskId(1);

        assertEquals(epic1, epic2, "Эпики с одинаковым id должны быть равны");
    }

    @Test
    void testEpicCannotAddToItself() {
        //Проверка, что объект task.Epic нельзя добавить в самого себя в виде подзадачи
        Epic epic = new Epic("основная задача 1", "основная задача №1");
        taskManager.addEpic(epic);
        int epicId = epic.getTaskId();

        SubTask subTask = new SubTask("подзадача 1", "подзадача №1", TaskStatus.NEW, epicId);
        subTask.setTaskId(epicId);
        assertThrows(IllegalArgumentException.class, () -> taskManager.addSubTask(subTask),
                "Добавление основной задачи в подзадачи!"
        );
    }

    @Test
    void testSubTaskCannotBeItsOwnEpic() {
        //Проверка, что объект Subtask нельзя сделать своим же эпиком
        int testId = 1;
        //без существующего epica subtask не создастся
        Epic epic = new Epic("основная задача", "основная задача");
        taskManager.addEpic(epic);
        testId = epic.getTaskId();

        SubTask subTask = new SubTask("подзадача 1", "подзадача №1", TaskStatus.NEW, testId);
        subTask.setTaskId(testId);

        assertThrows(IllegalArgumentException.class, () -> {
            taskManager.addSubTask(subTask);
        }, "Добавление подзадачи с epicId равным её собственному id");
    }
}
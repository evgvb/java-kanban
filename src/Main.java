import service.Managers;
import service.TaskManager;

import task.Epic;
import task.SubTask;
import task.Task;
import util.TaskStatus;


public class Main {

    public static void main(String[] args) {

        System.out.println("Поехали!");

        TaskManager taskManager = Managers.getDefault();

        //создание
        System.out.println("* Тестирование создания:");
        Task task1 = new Task("задача 1", "задача №1", TaskStatus.NEW);
        taskManager.addTask(task1);
        Task task2 = new Task("задача 2", "задача №2", TaskStatus.NEW);
        taskManager.addTask(task2);

        Epic epic1 = new Epic("основная задача 1", "основная задача №1");
        taskManager.addEpic(epic1);
        SubTask subTask1p1 = new SubTask("подзадача 1.1", "подзадача №1.1", TaskStatus.NEW, epic1.getTaskId());
        taskManager.addSubTask(subTask1p1);
        SubTask subTask1p2 = new SubTask("подзадача 1.2", "подзадача №1.2", TaskStatus.NEW, epic1.getTaskId());
        taskManager.addSubTask(subTask1p2);

        Epic epic2 = new Epic("основная задача 2", "основная задача №2");
        taskManager.addEpic(epic2);
        SubTask subTask2p1 = new SubTask("подзадача 2.1", "подзадача №2.1", TaskStatus.NEW, epic2.getTaskId());
        taskManager.addSubTask(subTask2p1);

        System.out.println("Задачи: " + taskManager.getAllTasks());
        System.out.println("Основные задачи: " + taskManager.getAllEpics());
        System.out.println("Подзадачи: " + taskManager.getAllSubTasks());

        //статус
        System.out.println("* Тестирование изменения статуса:");
        task1.setTaskStatus(TaskStatus.IN_PROGRESS);
        task2.setTaskStatus(TaskStatus.DONE);

        subTask1p1.setTaskStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubTask(subTask1p1);
        subTask1p2.setTaskStatus(TaskStatus.DONE);
        taskManager.updateSubTask(subTask1p2);

        subTask2p1.setTaskStatus(TaskStatus.DONE);
        taskManager.updateSubTask(subTask2p1);

        System.out.println("Задачи: " + taskManager.getAllTasks());
        System.out.println("Основные задачи: " + taskManager.getAllEpics());
        System.out.println("Подзадачи: " + taskManager.getAllSubTasks());

        //удаление
        System.out.println("* Тестирование удаления:");
        taskManager.deleteTask(task1.getTaskId());
        taskManager.deleteEpic(epic1.getTaskId());

        System.out.println("Задачи: " + taskManager.getAllTasks());
        System.out.println("Основные задачи: " + taskManager.getAllEpics());
        System.out.println("Подзадачи: " + taskManager.getAllSubTasks());

        //история просмотров
        System.out.println("* Тестирование истории:");
        taskManager.getTask(task2.getTaskId());
        taskManager.getEpic(epic1.getTaskId()); //удалена, в истории не отобразится
        taskManager.getEpic(epic2.getTaskId());
        taskManager.getSubTask(subTask2p1.getTaskId());

        System.out.println("История: " + taskManager.getHistory());

        //очистить историю
        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();

        //Дополнительное задание.
        //1. Создайте две задачи, эпик с тремя подзадачами и эпик без подзадач
        Task taskAdd1 = new Task("Задача доп 1", "Описание доп задачи 1", TaskStatus.NEW);
        taskManager.addTask(taskAdd1);
        Task taskAdd2 = new Task("Задача доп 2", "Описание доп задачи 2", TaskStatus.IN_PROGRESS);
        taskManager.addTask(taskAdd2);

        Epic epicWithSubTasks = new Epic("Основная доп задача с подзадачами", "доп эпик с подзадачами");
        taskManager.addEpic(epicWithSubTasks);

        SubTask subTaskAdd1 = new SubTask("Подзадача доп 1", "Описание доп подзадачи 1", TaskStatus.NEW, epicWithSubTasks.getTaskId());
        taskManager.addSubTask(subTaskAdd1);
        SubTask subTaskAdd2 = new SubTask("Подзадача доп 2", "Описание доп подзадачи 2", TaskStatus.IN_PROGRESS, epicWithSubTasks.getTaskId());
        taskManager.addSubTask(subTaskAdd2);
        SubTask subTaskAdd3 = new SubTask("Подзадача доп 3", "Описание доп подзадачи 3", TaskStatus.DONE, epicWithSubTasks.getTaskId());
        taskManager.addSubTask(subTaskAdd3);

        Epic epicWithoutSubTasks = new Epic("Основная доп задача без подзадач", "доп эпик без подзадач");
        taskManager.addEpic(epicWithoutSubTasks);

        System.out.println("Дополнительное задание");
        System.out.println("Задачи: " + taskManager.getAllTasks());
        System.out.println("Основные задачи: " + taskManager.getAllEpics());
        System.out.println("Подзадачи: " + taskManager.getAllSubTasks());
        System.out.println("История: " + taskManager.getHistory());

        //2. Запросите созданные задачи несколько раз в разном порядке
        System.out.println("Запрос задач в разном порядке");

        System.out.println("Вариант 1: задача 2 -> основная задача c подзадачами -> подзадача 1");
        taskManager.getTask(taskAdd2.getTaskId());
        taskManager.getEpic(epicWithSubTasks.getTaskId());
        taskManager.getSubTask(subTaskAdd1.getTaskId());
        System.out.println("История: " + taskManager.getHistory());

        System.out.println("Вариант 2: подзадача 3 -> задача 1 -> основная задача без подзадач -> задача 2");
        taskManager.getSubTask(subTaskAdd3.getTaskId());
        taskManager.getTask(task1.getTaskId());
        taskManager.getEpic(epicWithoutSubTasks.getTaskId());
        taskManager.getTask(task2.getTaskId()); // задача2 уже была в истории
        System.out.println("История: " + taskManager.getHistory());

        System.out.println("Вариант 3: подзадача 2 -> основная задача с подзадачами -> задача 1");
        taskManager.getSubTask(subTaskAdd2.getTaskId());
        taskManager.getEpic(epicWithSubTasks.getTaskId()); // эпик1 уже был в истории
        taskManager.getTask(task1.getTaskId()); // задача1 уже была в истории
        System.out.println("История: " + taskManager.getHistory());

        //4. Удалите задачу, которая есть в истории, и проверьте, что при печати она не будет выводиться.
        System.out.println("Удалите задачу, которая есть в истории, и проверьте, что при печати она не будет выводиться");
        System.out.println("Удаляем задачу1 (ID: " + taskAdd1.getTaskId() + ")");
        taskManager.deleteTask(taskAdd1.getTaskId());
        System.out.println("История после удаления задачи: " + taskManager.getHistory());

        //5. Удалите эпик с тремя подзадачами
        System.out.println("Удаляем эпик с подзадачами (ID: " + epicWithSubTasks.getTaskId() + ")");
        System.out.println("Подзадачи эпика: " + taskManager.getSubTasksByEpic(epicWithSubTasks.getTaskId()));
        taskManager.deleteEpic(epicWithSubTasks.getTaskId());
        System.out.println("История после удаления основной задачи: " + taskManager.getHistory());

        System.out.println("Задачи: " + taskManager.getAllTasks());
        System.out.println("Эпики: " + taskManager.getAllEpics());
        System.out.println("Подзадачи: " + taskManager.getAllSubTasks());
        System.out.println("История: " + taskManager.getHistory());

    }
}

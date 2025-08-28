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
        SubTask subTask1_1 = new SubTask("подзадача 1.1","подзадача №1.1", TaskStatus.NEW, epic1.getTaskId());
        taskManager.addSubTask(subTask1_1);
        SubTask subTask1_2 = new SubTask("подзадача 1.2","подзадача №1.2", TaskStatus.NEW, epic1.getTaskId());
        taskManager.addSubTask(subTask1_2);

        Epic epic2 = new Epic("основная задача 2", "основная задача №2");
        taskManager.addEpic(epic2);
        SubTask subTask2_1 = new SubTask("подзадача 2.1","подзадача №2.1", TaskStatus.NEW, epic2.getTaskId());
        taskManager.addSubTask(subTask2_1);


        System.out.println("Задачи: " + taskManager.getAllTasks());
        System.out.println("Основные задачи: " + taskManager.getAllEpics());
        System.out.println("Подзадачи: " + taskManager.getAllSubTasks());

        //статус
        System.out.println("* Тестирование изменения статуса:");
        task1.setTaskStatus(TaskStatus.IN_PROGRESS);
        task2.setTaskStatus(TaskStatus.DONE);

        subTask1_1.setTaskStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubTask(subTask1_1);
        subTask1_2.setTaskStatus(TaskStatus.DONE);
        taskManager.updateSubTask(subTask1_2);

        subTask2_1.setTaskStatus(TaskStatus.DONE);
        taskManager.updateSubTask(subTask2_1);

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
        taskManager.getSubTask(subTask2_1.getTaskId());

        System.out.println("История: " + taskManager.getHistory());

    }
}

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.HttpTaskServer;
import service.InMemoryTaskManager;
import service.TaskManager;
import task.Epic;
import task.SubTask;
import task.Task;
import util.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerEpicSubTaskTest {
    private TaskManager manager;
    private HttpTaskServer taskServer;
    private Gson gson;
    private HttpClient client;

    @BeforeEach
    void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(manager);
        gson = HttpTaskServer.getGson();
        client = HttpClient.newHttpClient();
        taskServer.start();
    }

    @AfterEach
    void tearDown() {
        taskServer.stop();
    }

    @Test
    void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("основная задача 1", "основная задача №1");
        String epicJson = gson.toJson(epic);

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Некорректный код ответа");

        List<Epic> epicsFromManager = manager.getAllEpics();
        assertEquals(1, epicsFromManager.size(), "Некорректное количество задач");
        assertEquals("основная задача 1", epicsFromManager.get(0).getTaskName(), "Некорректное имя задачи");
    }

    @Test
    void testAddSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic("основная задача 1", "основная задача №1");
        Epic createdEpic = manager.addEpic(epic);

        SubTask subTask = new SubTask("подзадача 1", "подзадача №1",
                TaskStatus.NEW, createdEpic.getTaskId());
        String subTaskJson = gson.toJson(subTask);

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subTaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Некорректный код ответа");

        List<SubTask> subTasksFromManager = manager.getAllSubTasks();
        assertEquals(1, subTasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("подзадача 1", subTasksFromManager.get(0).getTaskName(), "Некорректное имя подзадачи");
        assertEquals(createdEpic.getTaskId(), subTasksFromManager.get(0).getEpicId(), "Некорректный Epic ID");
    }

    @Test
    void testGetEpicSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("основная задача 1", "основная задача №1");
        Epic createdEpic = manager.addEpic(epic);

        SubTask subTask1 = new SubTask("подзадача 1", "подзадача №1", TaskStatus.NEW, createdEpic.getTaskId());
        SubTask subTask2 = new SubTask("подзадача 1", "подзадача №1", TaskStatus.IN_PROGRESS, createdEpic.getTaskId());
        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);

        URI url = URI.create("http://localhost:8080/epics/" + createdEpic.getTaskId() + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Некорректный код ответа");

        SubTask[] subTasks = gson.fromJson(response.body(), SubTask[].class);
        assertEquals(2, subTasks.length, "Некорректное количество подзадач");
    }

    @Test
    void testGetHistory() throws IOException, InterruptedException {
        Task task = new Task("задача 1", "задача №1", TaskStatus.NEW);
        Task createdTask = manager.addTask(task);
        manager.getTask(createdTask.getTaskId());

        Epic epic = new Epic("основная задача 1", "основная задача №1");
        Epic createdEpic = manager.addEpic(epic);
        manager.getEpic(createdEpic.getTaskId());

        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Некорректный код ответа");

        Task[] history = gson.fromJson(response.body(), Task[].class);
        assertEquals(2, history.length, "Некорректное количество задач в истории");
    }

    @Test
    void testGetPrioritizedTasks() throws IOException, InterruptedException {
        LocalDateTime now = LocalDateTime.now();

        Task task1 = new Task("Задача 1", "Первая задача", TaskStatus.NEW, now.plusHours(1), Duration.ofMinutes(45)); // Раннее время
        Task task2 = new Task("Задача 2", "Вторая задача", TaskStatus.NEW, now.plusHours(2), Duration.ofMinutes(30)); // Позднее время
        Task task3 = new Task("Задача 3", "Третья задача", TaskStatus.NEW, now.plusHours(3), Duration.ofMinutes(20)); // Самое позднее время

        //задачи не по порядку
        manager.addTask(task2);
        manager.addTask(task1);
        manager.addTask(task3);

        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Некорректный код ответа");

        Task[] prioritizedTasks = gson.fromJson(response.body(), Task[].class);

        assertNotNull(prioritizedTasks, "Нет приоритизированных задач");
        assertEquals(3, prioritizedTasks.length, "Некорректное количество задач");

        assertEquals("Задача 1", prioritizedTasks[0].getTaskName(), "Первая задача должна быть первой");
        assertEquals("Задача 2", prioritizedTasks[1].getTaskName(), "Вторая задача должна быть второй");
        assertEquals("Задача 3", prioritizedTasks[2].getTaskName(), "Третья задача должна быть третьей");
    }
}
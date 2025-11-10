import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.HttpTaskServer;
import service.InMemoryTaskManager;
import service.TaskManager;
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

class HttpTaskServerTest {
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
    void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("задача 1", "задача №1", TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(5));

        String taskJson = gson.toJson(task);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Некорректный код ответа");

        List<Task> tasksFromManager = manager.getAllTasks();

        assertNotNull(tasksFromManager, "Нет задачи");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("задача 1", tasksFromManager.get(0).getTaskName(), "Некорректное имя задачи");
    }

    @Test
    void testGetTask() throws IOException, InterruptedException {
        Task task = new Task("задача 1", "задача №1", TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        Task createdTask = manager.addTask(task);

        URI url = URI.create("http://localhost:8080/tasks/" + createdTask.getTaskId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Некорректный код ответа");

        Task taskFromResponse = gson.fromJson(response.body(), Task.class);
        assertNotNull(taskFromResponse, "Нет задачи");
        assertEquals(createdTask.getTaskId(), taskFromResponse.getTaskId(), "Некорректный ID задачи");
        assertEquals("задача 1", taskFromResponse.getTaskName(), "Некорректное имя задачи");
    }

    @Test
    void testGetAllTasks() throws IOException, InterruptedException {
        manager.addTask(new Task("задача 1", "задача №1", TaskStatus.NEW));
        manager.addTask(new Task("задача 2", "задача №2", TaskStatus.IN_PROGRESS));

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Некорректный код ответа");

        Task[] tasks = gson.fromJson(response.body(), Task[].class);
        assertNotNull(tasks, "Нет задач");
        assertEquals(2, tasks.length, "Некорректное количество задач");
    }

    @Test
    void testUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("задача 1", "задача №1",
                        TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        Task createdTask = manager.addTask(task);

        createdTask.setTaskName("задача 2");
        createdTask.setTaskStatus(TaskStatus.IN_PROGRESS);
        String updatedTaskJson = gson.toJson(createdTask);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(updatedTaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Некорректный код ответа");

        // Проверяем, что задача обновилась
        Task updatedTask = manager.getTask(createdTask.getTaskId());
        assertEquals("задача 2", updatedTask.getTaskName(), "Имя задачи не обновилось");
        assertEquals(TaskStatus.IN_PROGRESS, updatedTask.getTaskStatus(), "Статус задачи не обновился");
    }

    @Test
    void testDeleteTask() throws IOException, InterruptedException {
        Task taskToDelete = new Task("задача 1", "задача №1", TaskStatus.NEW,
                LocalDateTime.now().plusHours(1), Duration.ofMinutes(30));
        Task createdTask = manager.addTask(taskToDelete);
        int taskId = createdTask.getTaskId();

        assertEquals(1, manager.getAllTasks().size(), "Задача должна быть создана");

        URI url = URI.create("http://localhost:8080/tasks/" + taskId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Некорректный код ответа");
        assertEquals(0, manager.getAllTasks().size(), "Задача должна быть удалена из менеджера");
        assertEquals(0, manager.getPrioritizedTasks().size(),"Задача должна быть удалена из приоритизированного списка");
        assertEquals(0, manager.getHistory().size(), "Задача должна быть удалена из истории");
    }
}
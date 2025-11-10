package service;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import task.Task;
import util.TaskStatus;

import java.io.IOException;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public TaskHandler(TaskManager taskManager, Gson gson) {
        super(gson);
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");

            switch (method) {
                case "GET":
                    handleGet(exchange, pathParts);
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "DELETE":
                    handleDelete(exchange, pathParts);
                    break;
                default:
                    sendText(exchange, "Метод недопустим", 405);
            }
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    private void handleGet(HttpExchange exchange, String[] pathParts) throws IOException {
        if (pathParts.length == 2) {
            // GET /tasks
            String response = gson.toJson(taskManager.getAllTasks());
            sendText(exchange, response);
        } else if (pathParts.length == 3) {
            // GET /tasks/{id}
            try {
                int id = Integer.parseInt(pathParts[2]);
                Task task = taskManager.getTask(id);
                String response = gson.toJson(task);
                sendText(exchange, response);
            } catch (NumberFormatException e) {
                sendBadRequest(exchange, "Некорректный id");
            } catch (NotFoundException e) {
                sendNotFound(exchange);
            }
        } else {
            sendNotFound(exchange);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = readRequestBody(exchange);

        if (body == null || body.trim().isEmpty()) {
            sendBadRequest(exchange, "Ответ пустой");
            return;
        }

        try {
            Task task = gson.fromJson(body, Task.class);

            if (task == null || task.getTaskName() == null) {
                sendBadRequest(exchange, "Задача не получена");
                return;
            }

            if (task.getTaskStatus() == null) {
                task.setTaskStatus(TaskStatus.NEW);
            }

            Task result;
            if (task.getTaskId() == 0) {
                result = taskManager.addTask(task);
            } else {
                result = taskManager.updateTask(task);
            }

            sendSuccess(exchange, result.getTaskId());

        } catch (JsonSyntaxException e) {
            sendBadRequest(exchange, "Некорректный формат: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Задача пересекается по времени")) {
                sendHasInteractions(exchange);
            } else {
                sendBadRequest(exchange, e.getMessage());
            }
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (Exception e) {
            e.printStackTrace();
            sendInternalError(exchange);
        }
    }

    private void handleDelete(HttpExchange exchange, String[] pathParts) throws IOException {
        if (pathParts.length == 3) {
            try {
                int id = Integer.parseInt(pathParts[2]);
                taskManager.deleteTask(id);
                sendText(exchange, "Задача удалена");
            } catch (NumberFormatException e) {
                sendBadRequest(exchange, "Некорректный id");
            } catch (NotFoundException e) {
                sendNotFound(exchange);
            }
        } else {
            sendText(exchange, "Метод недопустим", 405);
        }
    }
}
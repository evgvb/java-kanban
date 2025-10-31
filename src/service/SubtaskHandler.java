package service;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import task.SubTask;

import java.io.IOException;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public SubtaskHandler(TaskManager taskManager, Gson gson) {
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
            String response = gson.toJson(taskManager.getAllSubTasks());
            sendText(exchange, response);
        } else if (pathParts.length == 3) {
            try {
                int id = Integer.parseInt(pathParts[2]);
                SubTask subTask = taskManager.getSubTask(id);
                if (subTask != null) {
                    String response = gson.toJson(subTask);
                    sendText(exchange, response);
                } else {
                    sendNotFound(exchange);
                }
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
            SubTask subTask = gson.fromJson(body, SubTask.class);
            if (subTask == null) {
                sendBadRequest(exchange, "Подзадача не получена");
                return;
            }

            SubTask result;
            if (subTask.getTaskId() == 0) {
                result = taskManager.addSubTask(subTask);
            } else {
                result = taskManager.updateSubTask(subTask);
            }

            if (result != null) {
                sendSuccess(exchange);
            } else {
                sendNotFound(exchange);
            }
        } catch (JsonSyntaxException e) {
            sendBadRequest(exchange, "Некорректный формат: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Задача пересекается по времени")) {
                sendHasInteractions(exchange);
            } else {
                sendBadRequest(exchange, e.getMessage());
            }
        }
    }

    private void handleDelete(HttpExchange exchange, String[] pathParts) throws IOException {
        if (pathParts.length == 3) {
            try {
                int id = Integer.parseInt(pathParts[2]);
                taskManager.deleteSubTask(id);
                sendText(exchange, "Подзадача удалнена");
            } catch (NumberFormatException e) {
                sendBadRequest(exchange, "Некорректный id");
            } catch (NotFoundException e) {
                sendNotFound(exchange);
            }
        } else {
            sendNotFound(exchange);
        }
    }
}
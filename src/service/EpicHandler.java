package service;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import task.Epic;

import java.io.IOException;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public EpicHandler(TaskManager taskManager, Gson gson) {
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
            String response = gson.toJson(taskManager.getAllEpics());
            sendText(exchange, response);
        } else if (pathParts.length == 3) {
            try {
                int id = Integer.parseInt(pathParts[2]);
                Epic epic = taskManager.getEpic(id);
                if (epic != null) {
                    String response = gson.toJson(epic);
                    sendText(exchange, response);
                } else {
                    sendNotFound(exchange);
                }
            } catch (NumberFormatException e) {
                sendBadRequest(exchange, "Некорректный id");
            } catch (NotFoundException e) {
                sendNotFound(exchange);
            }
        } else if (pathParts.length == 4 && "subtasks".equals(pathParts[3])) {
            try {
                int id = Integer.parseInt(pathParts[2]);
                Epic epic = taskManager.getEpic(id);
                if (epic != null) {
                    String response = gson.toJson(taskManager.getSubTasksByEpic(id));
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
            Epic epic = gson.fromJson(body, Epic.class);
            if (epic == null) {
                sendBadRequest(exchange, "Основная задача не получена");
                return;
            }

            Epic result;
            if (epic.getTaskId() == 0) {
                result = taskManager.addEpic(epic);
            } else {
                result = taskManager.updateEpic(epic);
            }

            if (result != null) {
                sendSuccess(exchange);
            } else {
                sendNotFound(exchange);
            }
        } catch (JsonSyntaxException e) {
            sendBadRequest(exchange, "Некорректный формат: " + e.getMessage());
        }
    }

    private void handleDelete(HttpExchange exchange, String[] pathParts) throws IOException {
        if (pathParts.length == 3) {
            try {
                int id = Integer.parseInt(pathParts[2]);
                taskManager.deleteEpic(id);
                sendText(exchange, "Основная задача удалена");
            } catch (NumberFormatException e) {
                sendBadRequest(exchange, "Некорректный id");
            }
        } else {
            sendNotFound(exchange);
        }
    }
}
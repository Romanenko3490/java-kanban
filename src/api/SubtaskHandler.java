package api;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import controllers.TaskManager;
import exceptions.TimeConflictException;
import models.*;
import api.dto.SubtaskResponseDto;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public SubtaskHandler(TaskManager taskManager) {
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
                    sendBadRequest(exchange);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendInternalError(exchange);
        }
    }

    private void handleGet(HttpExchange exchange, String[] pathParts) throws IOException {
        if (pathParts.length == 2) {
            // GET /subtasks - список всех подзадач
            List<SubtaskResponseDto> subtasks = taskManager.getSubtaskStore().stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            sendText(exchange, gson.toJson(subtasks), 200);
        } else if (pathParts.length == 3) {
            // GET /subtasks/{id}
            try {
                int id = Integer.parseInt(pathParts[2]);
                Optional<Subtask> subtask = taskManager.getSubtaskByID(id);
                if (subtask.isPresent()) {
                    sendText(exchange, gson.toJson(convertToDto(subtask.get())), 200);
                } else {
                    sendNotFound(exchange);
                }
            } catch (NumberFormatException e) {
                sendText(exchange, "Неверный формат id подзадачи", 400);
            }
        } else {
            sendBadRequest(exchange);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = readRequestBody(exchange.getRequestBody());
        try {
            JsonObject json = JsonParser.parseString(body).getAsJsonObject();

            // Валидация
            if (!json.has("name") || json.get("name").getAsString().isBlank()) {
                sendText(exchange, "Необходимо заполнить поле \"subtask name\"", 400);
                return;
            }

            if (!json.has("epicId")) {
                sendText(exchange, "Необходимо указать id Эпика", 400);
                return;
            }

            int epicId = json.get("epicId").getAsInt();
            Optional<Epic> epic = taskManager.getEpicByIdAvoidHistory(epicId);
            if (epic.isEmpty()) {
                sendText(exchange, "Епик не найден", 404);
                return;
            }

            // Создаем временную подзадачу для проверки конфликтов
            Subtask tempSubtask = new Subtask(
                    json.get("name").getAsString(),
                    json.has("description") ? json.get("description").getAsString() : "",
                    json.has("status") ? Status.valueOf(json.get("status").getAsString()) : Status.NEW,
                    json.has("startTime") ? json.get("startTime").getAsString() : null,
                    json.has("durationMinutes") ? json.get("durationMinutes").getAsInt() : 0
            );
            tempSubtask.setEpicID(epicId);
            AbstractTask.setIdCounter(AbstractTask.getIdCounter() - 1);

            // Если указано время, проверяем конфликты
            if (tempSubtask.getStartTime() != null) {
                try {
                    if (taskManager.timeConflictCheck(tempSubtask)) {
                        sendHasInteractions(exchange);
                        return;
                    }
                } catch (TimeConflictException e) {
                    sendHasInteractions(exchange);
                    return;
                }
            }

            if (json.has("id") && json.get("id").getAsInt() != 0) {
                // Обновление существующей подзадачи
                int id = json.get("id").getAsInt();
                Optional<Subtask> existingSubtask = taskManager.getSubtaskByID(id);
                if (existingSubtask.isEmpty()) {
                    sendNotFound(exchange);
                    return;
                }

                Subtask subtask = existingSubtask.get();
                updateSubtaskFromJson(subtask, json);
                taskManager.updateSubtask(
                        subtask.getId(),
                        subtask.getName(),
                        subtask.getDescription(),
                        subtask.getStatus(),
                        subtask.getStartTime(),
                        (int) subtask.getDuration().toMinutes()
                );
                sendText(exchange, gson.toJson(convertToDto(subtask)), 200);
            } else {
                // Создание новой подзадачи
                Subtask newSubtask = new Subtask(
                        json.get("name").getAsString(),
                        json.has("description") ? json.get("description").getAsString() : "",
                        json.has("status") ? Status.valueOf(json.get("status").getAsString()) : Status.NEW,
                        json.has("startTime") ? json.get("startTime").getAsString() : null,
                        json.has("durationMinutes") ? json.get("durationMinutes").getAsInt() : 0
                );
                newSubtask.setEpicID(epicId);
                taskManager.addNewSubtask(newSubtask);
                epic.get().addSubtask(newSubtask);
                sendText(exchange, gson.toJson(convertToDto(newSubtask)), 201);
            }
        } catch (JsonSyntaxException e) {
            sendText(exchange, "Неверный JSON формат", 400);
        } catch (TimeConflictException e) {
            sendHasInteractions(exchange);
        } catch (IllegalArgumentException e) {
            sendText(exchange, "Неверное значение поля \"status\"", 400);
        }
    }

    private void handleDelete(HttpExchange exchange, String[] pathParts) throws IOException {
        if (pathParts.length == 3) {
            try {
                int id = Integer.parseInt(pathParts[2]);
                Optional<Subtask> subtask = taskManager.getSubtaskByID(id);
                if (subtask.isEmpty()) {
                    sendNotFound(exchange);
                    return;
                }

                // Удаляем из эпика
                Optional<Epic> epic = taskManager.getEpicByID(subtask.get().getEpicID());
                epic.ifPresent(e -> e.deleteSubtask(id));

                // Удаляем из хранилища
                taskManager.deleteSubtaskByID(id);
                sendText(exchange, "Подзадача с id " + id + " успешно удалена.", 200);
            } catch (NumberFormatException e) {
                sendText(exchange, "Неверный формат id подзадачи.", 400);
            }
        } else {
            sendNotFound(exchange);
        }
    }

    private SubtaskResponseDto convertToDto(Subtask subtask) {
        return new SubtaskResponseDto(
                subtask.getId(),
                subtask.getName(),
                subtask.getDescription(),
                subtask.getStatus().toString(),
                subtask.getStartTime(),
                subtask.getDuration().toMinutes(),
                subtask.getEndTime(),
                subtask.getEpicID()
        );
    }

    private void updateSubtaskFromJson(Subtask subtask, JsonObject json) {
        if (json.has("name")) subtask.setName(json.get("name").getAsString());
        if (json.has("description")) subtask.setDescription(json.get("description").getAsString());
        if (json.has("status")) subtask.setStatus(Status.valueOf(json.get("status").getAsString()));
        if (json.has("startTime")) subtask.setStartTime(json.get("startTime").getAsString());
        if (json.has("durationMinutes")) subtask.setDuration(json.get("durationMinutes").getAsInt());
    }
}
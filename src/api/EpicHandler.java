package api;

import api.dto.SubtaskResponseDto;
import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import controllers.TaskManager;
import models.*;
import api.dto.EpicResponseDto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public EpicHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = new Gson();
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
                    sendNotFound(exchange);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendInternalError(exchange);
        }
    }

    private void handleGet(HttpExchange exchange, String[] pathParts) throws IOException {
        if (pathParts.length == 2) {
            // GET /epics - список всех эпиков
            List<EpicResponseDto> epics = taskManager.getEpicStore().stream()
                    .map(this::convertToDtoWithSubtasks)
                    .collect(Collectors.toList());
            sendText(exchange, gson.toJson(epics), 200);
        } else if (pathParts.length == 3) {
            // GET /epics/{id} - получение эпика с подзадачами
            try {
                int id = Integer.parseInt(pathParts[2]);
                Optional<Epic> epic = taskManager.getEpicByID(id);
                if (epic.isPresent()) {
                    EpicResponseDto dto = convertToDtoWithSubtasks(epic.get());
                    sendText(exchange, gson.toJson(dto), 200);
                } else {
                    sendNotFound(exchange);
                }
            } catch (NumberFormatException e) {
                sendText(exchange, "Неверный формат id Эпика", 400);
            }
        } else {
            sendNotFound(exchange);
        }
    }


    private void handlePost(HttpExchange exchange) throws IOException {
        String body = readRequestBody(exchange.getRequestBody());
        try {
            JsonObject json = JsonParser.parseString(body).getAsJsonObject();

            // Валидация
            if (!json.has("name") || json.get("name").getAsString().isBlank()) {
                sendText(exchange, "Необходимо заполнить поле \"epic name\"", 400);
                return;
            }

            if (json.has("id") && json.get("id").getAsInt() != 0) {
                // Обновление существующего эпика
                int id = json.get("id").getAsInt();
                Optional<Epic> existingEpic = taskManager.getEpicByID(id);
                if (existingEpic.isEmpty()) {
                    sendNotFound(exchange);
                    return;
                }

                Epic epic = existingEpic.get();
                updateEpicFromJson(epic, json);
                taskManager.updateEpic(epic.getId(), epic.getName(), epic.getDescription());
                sendText(exchange, gson.toJson(convertToDto(epic)), 200);
            } else {
                // Создание нового эпика
                Epic newEpic = new Epic(
                        json.get("name").getAsString(),
                        json.has("description") ? json.get("description").getAsString() : ""
                );
                taskManager.addNewEpic(newEpic);
                sendText(exchange, gson.toJson(convertToDto(newEpic)), 201);
            }
        } catch (JsonSyntaxException e) {
            sendText(exchange, "Неверный JSON формат", 400);
        }
    }

    private void handleDelete(HttpExchange exchange, String[] pathParts) throws IOException {
        if (pathParts.length == 3) {
            try {
                int id = Integer.parseInt(pathParts[2]);
                Optional<Epic> epic = taskManager.getEpicByID(id);
                if (epic.isEmpty()) {
                    sendNotFound(exchange);
                    return;
                }

                // Удаляем все подзадачи эпика
                List<Subtask> subtasksToDelete = new ArrayList<>(epic.get().getSubtaskList());
                subtasksToDelete.forEach(subtask -> taskManager.deleteSubtaskByID(subtask.getId()));

//                epic.get().getSubtaskList().forEach(subtask ->
//                        taskManager.deleteSubtaskByID(subtask.getId()));

                // Удаляем сам эпик
                taskManager.deleteEpicByID(id);
                sendText(exchange, "Эпик с id " + id + " успешно удален", 200);
            } catch (NumberFormatException e) {
                sendText(exchange, "Неверный формат id Эпика", 400);
            }
        } else {
            sendNotFound(exchange);
        }
    }

    private EpicResponseDto convertToDto(Epic epic) {
        return new EpicResponseDto(
                epic.getId(),
                epic.getName(),
                epic.getDescription(),
                epic.getStatus().toString(),
                epic.getStartTime(),
                epic.getDuration().toMinutes(),
                epic.getEndTime()
        );
    }

    private EpicResponseDto convertToDtoWithSubtasks(Epic epic) {
        // Основные данные эпика
        EpicResponseDto dto = new EpicResponseDto(
                epic.getId(),
                epic.getName(),
                epic.getDescription(),
                epic.getStatus().toString(),
                epic.getStartTime(),
                epic.getDuration().toMinutes(),
                epic.getEndTime()
        );

        // Добавляем подзадачи
        List<SubtaskResponseDto> subtasks = taskManager.getSubtasksFromEpic(epic.getId())
                .orElse(Collections.emptyList())
                .stream()
                .map(this::convertSubtaskToDto)
                .collect(Collectors.toList());

        dto.setSubtasks(subtasks);
        return dto;
    }

    private SubtaskResponseDto convertSubtaskToDto(Subtask subtask) {
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

    private void updateEpicFromJson(Epic epic, JsonObject json) {
        if (json.has("name")) epic.setName(json.get("name").getAsString());
        if (json.has("description")) epic.setDescription(json.get("description").getAsString());
    }
}
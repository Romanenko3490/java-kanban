package api;

import api.dto.TaskResponseDto;
import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import controllers.TaskManager;
import exceptions.TimeConflictException;
import models.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public TaskHandler(TaskManager taskManager) {
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
            // GET /tasks - список всех задач
            List<TaskResponseDto> tasks = taskManager.getTaskStore().stream()
                    .map(this::convertToResponseDto)
                    .collect(Collectors.toList());
            sendText(exchange, gson.toJson(tasks), 200);
        } else if (pathParts.length == 3) {
            // GET /tasks/{id} - получение задачи по ID
            try {
                int id = Integer.parseInt(pathParts[2]);
                Optional<Task> task = taskManager.getTaskByID(id);
                if (task.isPresent()) {
                    sendText(exchange, gson.toJson(convertToDto(task.get())), 200);
                } else {
                    sendNotFound(exchange);
                }
            } catch (NumberFormatException e) {
                sendText(exchange, "Неверный формат id задачи", 400);
            }
        } else {
            sendNotFound(exchange);
        }
    }

    private TaskResponseDto convertToResponseDto(Task task) {
        return new TaskResponseDto(
                task.getId(),
                task.getName(),
                task.getDescription(),
                task.getStatus().toString(),
                task.getStartTime(),
                task.getDuration(),
                task.getEndTime()
        );
    }


    private void handlePost(HttpExchange exchange) throws IOException {
        String body = readRequestBody(exchange.getRequestBody());
        try {
            TaskResponseDto taskDto = gson.fromJson(body, TaskResponseDto.class);

            // Валидация
            if (taskDto.getName() == null || taskDto.getName().isBlank()) {
                sendText(exchange, "Необходимо заполнить поле \"имя задачи\"", 400);
                return;
            }

            // Проверка пересечения по времени (только если указано время)
            if (taskDto.getStartTime() != null) {
                Task tempTask = new Task(
                        taskDto.getName(),
                        taskDto.getDescription() != null ? taskDto.getDescription() : "",
                        taskDto.getStatus() != null ? taskDto.getStatusInFormat() : Status.NEW,
                        taskDto.getStartTime(),
                        taskDto.getDurationMinutes() != null ? taskDto.getDurationMinutes().intValue() : 0
                );
                //уменьшаю каунтер, после создания временной задачи, которая нужна только для проверки на временном отрезке
                AbstractTask.setIdCounter(AbstractTask.getIdCounter() - 1);

                if (taskDto.getId() != null) {
                    tempTask.setId(taskDto.getId());
                }

                try {
                    if (taskManager.timeConflictCheck(tempTask)) {
                        sendHasInteractions(exchange);
                        return;
                    }
                } catch (TimeConflictException e) {
                    sendHasInteractions(exchange);
                    return;
                }
            }


            // Создаем или обновляем задачу
            if (taskDto.getId() != null && taskDto.getId() != 0) {
                // Логика обновления существующей задачи
                Optional<Task> existingTask = taskManager.getTaskByID(taskDto.getId());
                if (existingTask.isEmpty()) {
                    sendNotFound(exchange);
                    return;
                }

                Task task = existingTask.get();
                updateTaskFromDto(task, taskDto);
                taskManager.updateTask(
                        task.getId(),
                        task.getName(),
                        task.getDescription(),
                        task.getStatus(),
                        task.getStartTime(),
                        (int) task.getDuration().toMinutes()
                );
                sendText(exchange, gson.toJson(convertToDto(task)), 200);
            } else {
                // Создание новой задачи
                Task newTask = new Task(
                        taskDto.getName(),
                        taskDto.getDescription() != null ? taskDto.getDescription() : "",
                        taskDto.getStatus() != null ? taskDto.getStatusInFormat() : Status.NEW,
                        taskDto.getStartTime(),
                        taskDto.getDurationMinutes() != null ? taskDto.getDurationMinutes().intValue() : 0
                );
                taskManager.addNewTask(newTask);
                sendText(exchange, gson.toJson(convertToDto(newTask)), 201);
            }
        } catch (JsonSyntaxException e) {
            sendText(exchange, "Неверный JSON формат", 400);
        } catch (TimeConflictException e) {
            sendHasInteractions(exchange);
        }
    }

    private TaskResponseDto convertToDto(Task task) {
        TaskResponseDto dto = new TaskResponseDto(
                task.getId(),
                task.getName(),
                task.getDescription(),
                task.getStatus(),
                task.getStartTime(),
                task.getDuration().toMinutes(),
                task.getEndTime()
        );
        return dto;
    }

    private void updateTaskFromDto(Task task, TaskResponseDto dto) {
        if (dto.getName() != null) task.setName(dto.getName());
        if (dto.getDescription() != null) task.setDescription(dto.getDescription());
        if (dto.getStatus() != null) task.setStatus(dto.getStatusInFormat());
        if (dto.getStartTime() != null) task.setStartTime(dto.getStartTime());
        if (dto.getDurationMinutes() != null) {
            task.setDuration(dto.getDurationMinutes().intValue());
        }
    }

    private void handleDelete(HttpExchange exchange, String[] pathParts) throws IOException {
        if (pathParts.length == 3) {
            try {
                int id = Integer.parseInt(pathParts[2]);
                if (taskManager.getTaskByID(id).isPresent()) {
                    taskManager.deleteTaskByID(id);
                    sendText(exchange, "Задача с id " + id + " успешно удалена", 200);
                } else {
                    sendNotFound(exchange);
                }
            } catch (NumberFormatException e) {
                sendText(exchange, "Неверный формат id задачи", 400);
            }
        } else {
            sendNotFound(exchange);
        }
    }

}
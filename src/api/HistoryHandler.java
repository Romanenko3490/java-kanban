package api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import controllers.TaskManager;
import models.*;
import api.dto.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if ("GET".equals(exchange.getRequestMethod())) {
                handleGet(exchange);
            } else {
                sendBadRequest(exchange);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendInternalError(exchange);
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        List<TaskResponseDto> history = taskManager.getHistoryManager().getHistory().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        sendText(exchange, gson.toJson(history), 200);
    }

    private TaskResponseDto convertToDto(AbstractTask task) {
        if (task instanceof Task) {
            return new TaskResponseDto(
                    task.getId(),
                    task.getName(),
                    task.getDescription(),
                    task.getStatus().toString(),
                    task.getStartTime(),
                    task.getDuration().toMinutes(),
                    task.getEndTime()
            );
        } else if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
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
        } else if (task instanceof Epic) {
            return new EpicResponseDto(
                    task.getId(),
                    task.getName(),
                    task.getDescription(),
                    task.getStatus().toString(),
                    task.getStartTime(),
                    task.getDuration().toMinutes(),
                    task.getEndTime()
            );
        }
        return null;
    }
}
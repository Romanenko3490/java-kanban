package api;

import api.dto.SubtaskResponseDto;
import api.dto.TaskResponseDto;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import controllers.InMemoryTaskManager;
import models.AbstractTask;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class HistoryHandlerTest {
    private InMemoryTaskManager manager;
    private HttpTaskServer server;
    private HttpClient client;
    private Gson gson;

    public HistoryHandlerTest() throws IOException {
        this.manager = new InMemoryTaskManager();
        this.server = new HttpTaskServer(manager);
        this.client = HttpClient.newHttpClient();
        this.gson = new Gson();
    }


    @BeforeEach
    public void setUp() {
        AbstractTask.resetIdCounter();
        manager.clearTaskStore();
        manager.clearSubtaskStore();
        manager.clearEpicStore();
        manager.getHistoryManager().getHistory().clear();
        server.start();
    }

    @AfterEach
    public void shutDown() {
        server.stop();
    }

    @Test
    public void historyTest() throws IOException, InterruptedException {
        String epicJson = """
                {
                    "name": "Test Epic 1",
                    "description": "Test Epic Description"
                }
                """;

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .uri(URI.create("http://localhost:8080/epics"))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Неверный код статуса при создании эпика");

        String subtaskJson1 = """
                {
                    "name": "Test Subtask 1",
                    "description": "Test Subtask Description",
                    "status": "NEW",
                    "startTime": "25.10.2023 10:00",
                    "durationMinutes": 60,
                    "epicId": 1
                }
                """;

        String subtaskJson2 = """
                {
                    "name": "Test Subtask 2",
                    "description": "Test Subtask Description 2",
                    "status": "NEW",
                    "startTime": "25.10.2022 10:00",
                    "durationMinutes": 120,
                    "epicId": 1
                }
                """;

        //добавляю первую подзадачу
        HttpRequest requestPostSubtask1 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson1))
                .uri(URI.create("http://localhost:8080/subtasks"))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> responsePostSubtask1 = client.send(requestPostSubtask1, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, responsePostSubtask1.statusCode(), "Неверный код при добавлении подзадачи");

        //добавляю вторую подзадачу
        HttpRequest requestPostSubtask2 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson2))
                .uri(URI.create("http://localhost:8080/subtasks"))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> responsePostSubtask2 = client.send(requestPostSubtask2, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, responsePostSubtask2.statusCode(), "Неверный код при добавлении подзадачи");

        String jsonTask = """
                    {
                    "name": "Time Conflict Task",
                    "description": "Description",
                    "status": "IN_PROGRESS",
                    "startTime": "24.10.1991 12:59",
                    "durationMinutes": 120
                }
                """;

        HttpRequest requestJsonTask = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .header("Content-Type", "application/json")
                .uri(URI.create("http://localhost:8080/tasks"))
                .build();

        HttpResponse<String> responseJsonTask = client.send(requestJsonTask, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, responseJsonTask.statusCode(), "Неверный код статуса при создании");

        //Выстраиваем историю с помощью GET запросов

        HttpRequest taskRequest = HttpRequest.newBuilder()
                .GET()
                .header("Content-Type", "application/json")
                .uri(URI.create("http://localhost:8080/tasks/4"))
                .build();

        HttpResponse<String> taskResponse = client.send(taskRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, taskResponse.statusCode());

        HttpRequest subtask2Req = HttpRequest.newBuilder()
                .GET()
                .header("Content-Type", "application/json")
                .uri(URI.create("http://localhost:8080/subtasks/3"))
                .build();

        HttpResponse<String> subtask2Response = client.send(subtask2Req, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, subtask2Response.statusCode());

        assertEquals(2, manager.getHistoryManager().getHistory().size(), "Неверный размер истории");

        List<Integer> expectedHistoryOrder = List.of(4, 3); //Ожидаемы порядок

        //проверка порядка в истории
        List<Integer> actualHistoryIds = manager.getHistoryManager().getHistory().stream()
                .map(AbstractTask::getId)
                .collect(Collectors.toList());
        assertEquals(expectedHistoryOrder, actualHistoryIds, "Неверный порядок в истории");


        HttpRequest historyReq = HttpRequest.newBuilder()
                .GET()
                .header("Content-Type", "application/json")
                .uri(URI.create("http://localhost:8080/history"))
                .build();

        HttpResponse<String> historyRes = client.send(historyReq, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, historyRes.statusCode(), "Неверный код статуса при получении истории");

        List<TaskResponseDto> historyList = gson.fromJson(historyRes.body(), new HistoryListTypeToken().getType());

        assertNotNull(historyList, "История не должна быть null");
        assertEquals(2, historyList.size(), "Неверное количество элементов в истории");
        assertEquals(4, historyList.getFirst().getId(), "Первым должен быть task с id=4");
        assertEquals(3, historyList.get(1).getId(), "Вторым должен быть subtask с id=3");
    }

    class HistoryListTypeToken extends TypeToken<List<TaskResponseDto>> {

    }

}
package api;

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

import static org.junit.jupiter.api.Assertions.*;

class PrioritizedHandlerTest {
    private InMemoryTaskManager manager;
    private HttpTaskServer server;
    private HttpClient client;
    private Gson gson;

    public PrioritizedHandlerTest() throws IOException {
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
    public void shellReturnTasksByPriority() throws IOException, InterruptedException {
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
                    "name": "25.10.2023 10:00 Subtask 1",
                    "description": "Test Subtask Description",
                    "status": "NEW",
                    "startTime": "25.10.2023 10:00",
                    "durationMinutes": 60,
                    "epicId": 1
                }
                """;

        String subtaskJson2 = """
                {
                    "name": "25.10.2023 07:00 Subtask 2",
                    "description": "Test Subtask Description 2",
                    "status": "NEW",
                    "startTime": "25.10.2023 07:00",
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
                    "name": "25.10.2023 00:01 Task",
                    "description": "Description",
                    "status": "IN_PROGRESS",
                    "startTime": "25.10.2023 00:01",
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


        HttpRequest requestPriority = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .build();

        HttpResponse<String> priorityResponse = client.send(requestPriority, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, priorityResponse.statusCode(), "Неверный код статуса при получении списка по приоритету");

        List<TaskResponseDto> prioritizedList = gson.fromJson(priorityResponse.body(), new TypePriorityTokenList().getType());

        assertEquals(3, prioritizedList.size());
        assertEquals("25.10.2023 00:01 Task", prioritizedList.get(0).getName());
        assertEquals("25.10.2023 07:00 Subtask 2", prioritizedList.get(1).getName());
        assertEquals("25.10.2023 10:00 Subtask 1", prioritizedList.get(2).getName());

    }

    class TypePriorityTokenList extends TypeToken<List<TaskResponseDto>> {

    }

}
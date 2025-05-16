package api;


import api.dto.TaskResponseDto;
import com.google.gson.Gson;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.reflect.TypeToken;
import controllers.InMemoryTaskManager;
import models.AbstractTask;
import models.Status;
import models.Task;
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
import java.util.Optional;

class TaskHandlerTest {
    private InMemoryTaskManager manager;
    private HttpTaskServer server;
    private HttpClient client;
    private Gson gson;


    TaskHandlerTest() throws IOException {
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
        server.start();
    }

    @AfterEach
    public void shutDown() {
        server.stop();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        TaskResponseDto taskDto = new TaskResponseDto(
                0, // ID будет назначен сервером
                "task 1",
                "Description task 1",
                Status.NEW,
                "24.10.1991 12:00",
                60, // Используем long вместо Duration
                "24.10.1991 13:00"
        );

        String taskJson = gson.toJson(taskDto);

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .uri(URI.create("http://localhost:8080/tasks"))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Неверный код статуса при создании");

        List<Task> taskFromManager = manager.getTaskStore();
        assertNotNull(taskFromManager, "Задачи не возвращаются");
        assertEquals(1, taskFromManager.size(), "Некорректное количество задач");

        Task savedTask = taskFromManager.get(0);
        assertAll(
                () -> assertEquals("task 1", savedTask.getName()),
                () -> assertEquals("Description task 1", savedTask.getDescription()),
                () -> assertEquals(Status.NEW, savedTask.getStatus()),
                () -> assertEquals(60, savedTask.getDuration().toMinutes()),
                () -> assertEquals("24.10.1991 12:00", savedTask.getStartTime()),
                () -> assertEquals("24.10.1991 13:00", savedTask.getEndTime()),
                () -> assertEquals(1, savedTask.getId())
        );

    }

    @Test
    public void getMethodByIdShellReturnSameTaskAsSaved() throws IOException, InterruptedException {
        TaskResponseDto taskDto = new TaskResponseDto(
                0,
                "task 1",
                "Description task 1",
                Status.NEW,
                "24.10.1991 12:00",
                60, // Используем long вместо Duration
                "24.10.1991 13:00"
        );

        String taskJson = gson.toJson(taskDto);

        HttpRequest requestPost = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .header("Content-Type", "application/json")
                .uri(URI.create("http://localhost:8080/tasks"))
                .build();

        HttpResponse<String> responsePost = client.send(requestPost, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, responsePost.statusCode(), "Неверный код статуса при создании");

        HttpRequest requestGet = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/tasks/1"))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> responseGet = client.send(requestGet, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseGet.statusCode(), "Неверный код статуса при получении задачи");

        TaskResponseDto dto = gson.fromJson(responseGet.body(), TaskResponseDto.class);

        assertAll(
                () -> assertEquals(1, dto.getId()),
                () -> assertEquals("task 1", dto.getName()),
                () -> assertEquals("Description task 1", dto.getDescription()),
                () -> assertEquals(Status.NEW.toString(), dto.getStatus()),
                () -> assertEquals(60, dto.getDurationMinutes()),
                () -> assertEquals("24.10.1991 13:00", dto.getEndTime())
        );

        //проверка через менеджер
        Optional<Task> taskFromManager = manager.getTaskByID(1);
        assertTrue(taskFromManager.isPresent(), "Задача должна существовать в менеджере");
        Task savedTaskInManager = taskFromManager.get();

        assertAll(
                () -> assertEquals(savedTaskInManager.getId(), dto.getId()),
                () -> assertEquals(savedTaskInManager.getName(), dto.getName()),
                () -> assertEquals(savedTaskInManager.getDescription(), dto.getDescription()),
                () -> assertEquals(savedTaskInManager.getStartTime(), dto.getStartTime()),
                () -> assertEquals(savedTaskInManager.getDuration().toMinutes(), dto.getDurationMinutes()),
                () -> assertEquals(savedTaskInManager.getEndTime(), dto.getEndTime())
        );
    }


    @Test
    public void shellBeUpdated() throws IOException, InterruptedException {
        TaskResponseDto taskDto = new TaskResponseDto(
                0, // ID будет назначен сервером
                "task 1",
                "Description task 1",
                Status.NEW,
                "24.10.1991 12:00",
                60, // Используем long вместо Duration
                "24.10.1991 13:00"
        );

        String taskJson = gson.toJson(taskDto);

        HttpRequest requestPost = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .header("Content-Type", "application/json")
                .uri(URI.create("http://localhost:8080/tasks"))
                .build();

        HttpResponse<String> responsePost = client.send(requestPost, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, responsePost.statusCode(), "Неверный код статуса при создании");


        String jsonForUpdate = """
                    {
                    "id": "1",
                    "name": "Updated Task",
                    "description": "Updated Description",
                    "status": "IN_PROGRESS",
                    "startTime": "25.10.2023 10:00",
                    "durationMinutes": 120
                }
                """;

        HttpRequest requestUpdateTask = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(jsonForUpdate))
                .header("Content-Type", "application/json")
                .uri(URI.create("http://localhost:8080/tasks/1"))
                .build();

        HttpResponse<String> responseUpdateTask = client.send(requestUpdateTask, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseUpdateTask.statusCode(), "Неверный код статуса при создании");

        TaskResponseDto dtoTask = gson.fromJson(responseUpdateTask.body(), TaskResponseDto.class);

        assertAll(
                () -> assertEquals(1, dtoTask.getId()),
                () -> assertEquals("Updated Task", dtoTask.getName()),
                () -> assertEquals("Updated Description", dtoTask.getDescription()),
                () -> assertEquals(Status.IN_PROGRESS, dtoTask.getStatusInFormat()),
                () -> assertEquals("25.10.2023 10:00", dtoTask.getStartTime()),
                () -> assertEquals(120, dtoTask.getDurationMinutes()),
                () -> assertEquals("25.10.2023 12:00", dtoTask.getEndTime())
        );


    }


    @Test
    public void testOfDeleteTaskRequestMethod() throws IOException, InterruptedException {
        TaskResponseDto taskDto = new TaskResponseDto(
                0,
                "task 1",
                "Description task 1",
                Status.NEW,
                "24.10.1991 12:00",
                60, // Используем long вместо Duration
                "24.10.1991 13:00"
        );

        String taskJson = gson.toJson(taskDto);

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .header("Content-Type", "application/json")
                .uri(URI.create("http://localhost:8080/tasks"))
                .build();

        HttpResponse<String> responseGet = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, responseGet.statusCode(), "Неверный код статуса при создании");


        HttpRequest requestToDelete = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create("http://localhost:8080/tasks/1"))
                .build();

        HttpResponse<String> responseDelete = client.send(requestToDelete, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseDelete.statusCode(), "Неверный код статуса при получении удалении");

        //Проверка пустого таскостора в менеджере
        List<Task> taskStoreFromManager = manager.getTaskStore();
        assertEquals(0, taskStoreFromManager.size());

        HttpRequest requestOfTaskStore = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/tasks"))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> taskStoreResponse = client.send(requestOfTaskStore, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, taskStoreResponse.statusCode());

        List<Task> taskStoreList = gson.fromJson(taskStoreResponse.body(), new TaskListTypeToken().getType());
        assertEquals(0, taskStoreList.size());


    }

    @Test
    public void taskShellNotBeAddedIfTimeConflictExists() throws IOException, InterruptedException {
        TaskResponseDto taskDto = new TaskResponseDto(
                0,
                "task 1",
                "Description task 1",
                Status.NEW,
                "24.10.1991 12:00",
                60, // Используем long вместо Duration
                "24.10.1991 13:00"
        );

        String taskJson = gson.toJson(taskDto);

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .header("Content-Type", "application/json")
                .uri(URI.create("http://localhost:8080/tasks"))
                .build();

        HttpResponse<String> responseGet = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, responseGet.statusCode(), "Неверный код статуса при создании");

        List<Task> taskStore = manager.getTaskStore();
        assertEquals(1, taskStore.size());

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
        assertEquals(406, responseJsonTask.statusCode(), "Неверный код статуса при создании");

        taskStore = manager.getTaskStore();
        assertEquals(1, taskStore.size());

    }

    static class TaskListTypeToken extends TypeToken<List<TaskResponseDto>> {

    }
}
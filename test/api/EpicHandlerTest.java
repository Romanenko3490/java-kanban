package api;

import api.dto.EpicResponseDto;
import api.dto.SubtaskResponseDto;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import controllers.InMemoryTaskManager;
import models.*;
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

class EpicHandlerTest {
    private InMemoryTaskManager manager;
    private HttpTaskServer server;
    private HttpClient client;
    private Gson gson;

    public EpicHandlerTest() throws IOException {
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
    public void testAddEpic() throws IOException, InterruptedException {
        String epicJson = """
                {
                    "name": "Test Epic",
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

        //проверяем что эпик добавлен в менеджер
        List<Epic> epicStore = manager.getEpicStore();
        assertEquals(1, epicStore.size());

        //првоеряем ответ
        EpicResponseDto responseDto = gson.fromJson(response.body(), EpicResponseDto.class);
        assertNotNull(responseDto, "Ответ не содержит данных об эпике");
        assertEquals("Test Epic", responseDto.getName(), "Неверное имя эпика");
        assertEquals("Test Epic Description", responseDto.getDescription(), "Неверное описание эпика");
        assertEquals("NEW", responseDto.getStatus(), "Неверный статус эпика");

        HttpRequest requestGet = HttpRequest.newBuilder()
                .GET()
                .header("Content-Type", "application/json")
                .uri(URI.create("http://localhost:8080/epics/1"))
                .build();

        HttpResponse<String> responseGet = client.send(requestGet, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseGet.statusCode(), "Неверный код статуса при получении эпика");

        EpicResponseDto getResponseDto = gson.fromJson(responseGet.body(), EpicResponseDto.class);
        assertAll(
                () -> assertEquals(responseDto.getId(), getResponseDto.getId()),
                () -> assertEquals(responseDto.getName(), getResponseDto.getName()),
                () -> assertEquals(responseDto.getDescription(), getResponseDto.getDescription()),
                () -> assertEquals(responseDto.getStatus(), getResponseDto.getStatus()),
                () -> assertEquals(responseDto.getStartTime(), getResponseDto.getStartTime()),
                () -> assertEquals(responseDto.getDurationMinutes(), getResponseDto.getDurationMinutes()),
                () -> assertEquals(responseDto.getEndTime(), getResponseDto.getEndTime())
        );
    }

    @Test
    public void testDeleteEpicById() throws IOException, InterruptedException {
        String epicJson = """
                {
                    "name": "Test Epic",
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

        //проверяем что эпик добавлен в менеджер
        List<Epic> epicStore = manager.getEpicStore();
        assertEquals(1, epicStore.size());

        HttpRequest requestToDelete = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create("http://localhost:8080/epics/1"))
                .build();

        HttpResponse<String> deleteResponse = client.send(requestToDelete, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, deleteResponse.statusCode(), "Неверный код при удалении эпика");

        epicStore = manager.getEpicStore();
        assertEquals(0, epicStore.size());

        HttpRequest requestGet = HttpRequest.newBuilder()
                .GET()
                .header("Content-Type", "application/json")
                .uri(URI.create("http://localhost:8080/epics/1"))
                .build();

        HttpResponse<String> responseGet = client.send(requestGet, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, responseGet.statusCode());
    }

    @Test
    public void addSubtaskToEpic() throws IOException, InterruptedException {
        String epicJson = """
                {
                    "name": "Test Epic",
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
                    "name": "Test Subtask",
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

        //проверяю менеджер
        List<Subtask> subtaskStore = manager.getSubtaskStore();
        List<Epic> epicStore = manager.getEpicStore();
        assertEquals(2, subtaskStore.size());
        assertEquals(1, epicStore.size());

        //Сравниваю подзадачи из менеджера и полученых запросом GET
        Subtask subtaskFromManager1 = subtaskStore.get(0);
        Subtask subtaskFromManager2 = subtaskStore.get(1);

        //получаем первую подзадачу методом GET
        HttpRequest requestSubtask1 = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/subtasks/2"))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> getResponseSubtask1 = client.send(requestSubtask1, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getResponseSubtask1.statusCode(), "Неверный код при получении подзадачи 1");

        SubtaskResponseDto gotSubtask1 = gson.fromJson(getResponseSubtask1.body(), SubtaskResponseDto.class);

        //получаем вторую подзадачу методом GET
        HttpRequest requestSubtask2 = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/subtasks/3"))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> getResponseSubtask2 = client.send(requestSubtask2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getResponseSubtask2.statusCode(), "Неверный код при получении подзадачи 2");

        SubtaskResponseDto gotSubtask2 = gson.fromJson(getResponseSubtask2.body(), SubtaskResponseDto.class);

        assertAll(
                () -> assertEquals(subtaskFromManager1.getId(), gotSubtask1.getId()),
                () -> assertEquals(subtaskFromManager1.getName(), gotSubtask1.getName()),
                () -> assertEquals(subtaskFromManager1.getDescription(), gotSubtask1.getDescription()),
                () -> assertEquals(subtaskFromManager1.getStatus(), gotSubtask1.getStatusInFormat()),
                () -> assertEquals(subtaskFromManager1.getStartTime(), gotSubtask1.getStartTime()),
                () -> assertEquals(subtaskFromManager1.getDuration().toMinutes(), gotSubtask1.getDurationMinutes()),
                () -> assertEquals(subtaskFromManager1.getEndTime(), gotSubtask1.getEndTime()),
                () -> assertEquals(subtaskFromManager1.getEpicID(), gotSubtask1.getEpicId())
        );

        assertAll(
                () -> assertEquals(subtaskFromManager2.getId(), gotSubtask2.getId()),
                () -> assertEquals(subtaskFromManager2.getName(), gotSubtask2.getName()),
                () -> assertEquals(subtaskFromManager2.getDescription(), gotSubtask2.getDescription()),
                () -> assertEquals(subtaskFromManager2.getStatus(), gotSubtask2.getStatusInFormat()),
                () -> assertEquals(subtaskFromManager2.getStartTime(), gotSubtask2.getStartTime()),
                () -> assertEquals(subtaskFromManager2.getDuration().toMinutes(), gotSubtask2.getDurationMinutes()),
                () -> assertEquals(subtaskFromManager2.getEndTime(), gotSubtask2.getEndTime()),
                () -> assertEquals(subtaskFromManager2.getEpicID(), gotSubtask2.getEpicId())
        );

    }

    @Test
    public void ifDeleteEpicAllHisSubtasksShellBeDeleted() throws IOException, InterruptedException {
        String epicJson = """
                {
                    "name": "Test Epic",
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
                    "name": "Test Subtask",
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

        //проверяю менеджер
        List<Subtask> subtaskStore = manager.getSubtaskStore();
        List<Epic> epicStore = manager.getEpicStore();
        assertEquals(2, subtaskStore.size());
        assertEquals(1, epicStore.size());

        //удаляем Эпик
        HttpRequest requestToDelete = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create("http://localhost:8080/epics/1"))
                .build();

        HttpResponse<String> deleteResponse = client.send(requestToDelete, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, deleteResponse.statusCode(), "Неверный код при удалении эпика");

        epicStore = manager.getEpicStore();
        assertEquals(0, epicStore.size());
        subtaskStore = manager.getSubtaskStore();
        assertEquals(0, subtaskStore.size());

        //проверяем методы GET
        HttpRequest requestGet = HttpRequest.newBuilder()
                .GET()
                .header("Content-Type", "application/json")
                .uri(URI.create("http://localhost:8080/epics/1"))
                .build();

        HttpResponse<String> responseGet = client.send(requestGet, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, responseGet.statusCode());

        HttpRequest requestSubtask1 = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/subtasks/2"))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> getResponseSubtask1 = client.send(requestSubtask1, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, getResponseSubtask1.statusCode(), "Неверный код при получении подзадачи 1");

        HttpRequest requestSubtask2 = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/subtasks/3"))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> getResponseSubtask2 = client.send(requestSubtask2, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, getResponseSubtask2.statusCode(), "Неверный код при получении подзадачи 2");

    }

    @Test
    public void testOfUpdateEpicAndSubtask() throws IOException, InterruptedException {
        String epicJson = """
                {
                    "name": "Test Epic",
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
                    "name": "Test Subtask",
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

        //проверяю менеджер
        List<Subtask> subtaskStore = manager.getSubtaskStore();
        List<Epic> epicStore = manager.getEpicStore();
        assertEquals(2, subtaskStore.size());
        assertEquals(1, epicStore.size());

        //Изменяем поля эпика и подзадачи 1
        String updateEpicJson = """
                {
                    "name": "Updated Epic",
                    "description": "Updated Epic Description"
                }
                """;

        HttpRequest updateEpicRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(updateEpicJson))
                .uri(URI.create("http://localhost:8080/epics/1"))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> updateEpicResponse = client.send(updateEpicRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, updateEpicResponse.statusCode(), "Неверный код статуса при создании эпика");

        String updateSubtaskJson1 = """
                {
                    "id":2,
                    "name": "Test Subtask",
                    "description": "Test Subtask Description",
                    "status": "NEW",
                    "epicId": 1
                }
                """;

        HttpRequest updateRequestPostSubtask1 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(updateSubtaskJson1))
                .uri(URI.create("http://localhost:8080/subtasks/2"))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> responseUpdatePostSubtask1 = client.send(updateRequestPostSubtask1, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseUpdatePostSubtask1.statusCode(), "Неверный код при добавлении подзадачи");

        //проверяю, что количество эпиков и подзадач не изменилось
        subtaskStore = manager.getSubtaskStore();
        epicStore = manager.getEpicStore();
        assertEquals(2, subtaskStore.size());
        assertEquals(2, epicStore.size());

        //проверяю объекты полученые запросом GET и в мееджере
        Epic epicFromManager = epicStore.get(0);
        Subtask subtaskFromManager = subtaskStore.get(0);

        HttpRequest requestUpdatedEpic = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/epics/1"))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> responseUpdatedEpic = client.send(requestUpdatedEpic, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseUpdatedEpic.statusCode());

        EpicResponseDto epicDto = gson.fromJson(responseUpdatedEpic.body(), EpicResponseDto.class);
        assertAll(
                () -> assertEquals(epicFromManager.getId(), epicDto.getId()),
                () -> assertEquals(epicFromManager.getName(), epicDto.getName()),
                () -> assertEquals(epicFromManager.getDescription(), epicDto.getDescription()),
                () -> assertEquals(epicFromManager.getStatus(), epicDto.getStatusInFormat()),
                () -> assertEquals(epicFromManager.getStartTime(), epicDto.getStartTime()),
                () -> assertEquals(epicFromManager.getDuration().toMinutes(), epicDto.getDurationMinutes()),
                () -> assertEquals(epicFromManager.getEndTime(), epicDto.getEndTime())
        );

        HttpRequest requestUpdatedSubtask = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/subtasks/2"))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> responseUpdateSubtask = client.send(requestUpdatedSubtask, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseUpdateSubtask.statusCode());

        SubtaskResponseDto subtaskDto = gson.fromJson(responseUpdateSubtask.body(), SubtaskResponseDto.class);
        assertAll(
                () -> assertEquals(subtaskFromManager.getId(), subtaskDto.getId()),
                () -> assertEquals(subtaskFromManager.getName(), subtaskDto.getName()),
                () -> assertEquals(subtaskFromManager.getDescription(), subtaskDto.getDescription()),
                () -> assertEquals(subtaskFromManager.getStatus(), subtaskDto.getStatusInFormat()),
                () -> assertEquals(subtaskFromManager.getStartTime(), subtaskDto.getStartTime()),
                () -> assertEquals(subtaskFromManager.getDuration().toMinutes(), subtaskDto.getDurationMinutes()),
                () -> assertEquals(subtaskFromManager.getEndTime(), subtaskDto.getEndTime()),
                () -> assertEquals(subtaskFromManager.getEpicID(), subtaskDto.getEpicId())
        );
    }

    @Test
    public void shellReturnListOfEpicsAndListOfEpicsSubtasks() throws IOException, InterruptedException {
        String epicJson1 = """
                {
                    "name": "Test Epic",
                    "description": "Test Epic Description"
                }
                """;

        String epicJson2 = """
                {
                    "name": "Test Epic",
                    "description": "Test Epic Description"
                }
                """;

        HttpRequest requestEpic1 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(epicJson1))
                .uri(URI.create("http://localhost:8080/epics"))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> responseEpic1 = client.send(requestEpic1, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, responseEpic1.statusCode(), "Неверный код статуса при создании эпика");


        HttpRequest requestEpic2 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(epicJson2))
                .uri(URI.create("http://localhost:8080/epics"))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> responseEpic2 = client.send(requestEpic2, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, responseEpic2.statusCode(), "Неверный код статуса при создании эпика");

        String subtaskJson1 = """
                {
                    "name": "Test Subtask",
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


        //Запрашиваем список эпиков, должен быть такой же как и в менеджере
        List<Epic> epicsFromManager = manager.getEpicStore();

        HttpRequest epicsRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/epics"))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> epicListResponse = client.send(epicsRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, epicListResponse.statusCode());

        List<EpicResponseDto> dtoEpicList = gson.fromJson(epicListResponse.body(), new EpicListType().getType());
        assertEquals(dtoEpicList.size(), epicsFromManager.size());

        //Получаем список подзадач по id Эпика
        List<Subtask> subtaskListFromManager = manager.getSubtaskStore();

        HttpRequest subtaskStoreRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/epics/1"))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> subtaskStoreResponse = client.send(subtaskStoreRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, subtaskStoreResponse.statusCode());

        EpicResponseDto epicDto = gson.fromJson(subtaskStoreResponse.body(), EpicResponseDto.class);
        List<SubtaskResponseDto> subtaskDtoList = epicDto.getSubtasks();
        assertEquals(subtaskListFromManager.size(), subtaskDtoList.size(), "Размеры списков подзадач не сходятся");

    }

    @Test
    public void subtasShellNotBeAddedIfTimeConflictWithTaskOrSubtask() throws IOException, InterruptedException {
        String epicJson = """
                {
                    "name": "Test Epic",
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
                    "name": "Test Subtask",
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
                    "startTime": "25.10.2023 10:00",
                    "durationMinutes": 120,
                    "epicId": 1
                }
                """;

        HttpRequest requestPostSubtask1 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson1))
                .uri(URI.create("http://localhost:8080/subtasks"))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> responsePostSubtask1 = client.send(requestPostSubtask1, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, responsePostSubtask1.statusCode(), "Неверный код при добавлении подзадачи");

        HttpRequest requestPostSubtask2 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson2))
                .uri(URI.create("http://localhost:8080/subtasks"))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> responsePostSubtask2 = client.send(requestPostSubtask2, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, responsePostSubtask2.statusCode(), "Неверный код при добавлении подзадачи");


        String jsonTask = """
                    {
                    "name": "Time Conflict Task",
                    "description": "Description",
                    "status": "IN_PROGRESS",
                    "startTime": "25.10.2023 10:00",
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


    }

    static class EpicListType extends TypeToken<List<EpicResponseDto>> {

    }

}
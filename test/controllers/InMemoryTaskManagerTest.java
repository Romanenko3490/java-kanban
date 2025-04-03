package controllers;

import models.*;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    InMemoryTaskManager inMemoryTaskManager;
    Epic epic;
    Task task;
    Subtask subtask;

    public InMemoryTaskManagerTest() {
        this.inMemoryTaskManager = new InMemoryTaskManager();
        this.epic = new Epic("Epic", "Epic Description");
        this.task = new Task("Task", "Task Description");
        this.subtask = new Subtask("Subtask", "Subtask description");

        inMemoryTaskManager.addNewSubtask(subtask);
        epic.addSubtask(subtask);
        inMemoryTaskManager.addNewTask(task);
        inMemoryTaskManager.addNewEpic(epic);
    }


    @AfterEach
    public void resetIdCounter() {
        AbstractTask.resetIdCounter();
    }


    @Test
    public void mapsShellNotBeNull() {
        assertNotNull(inMemoryTaskManager.getEpicStore());
        assertNotNull(inMemoryTaskManager.getTaskStore());
        assertNotNull(inMemoryTaskManager.getSubtaskStore());
    }

    @Test
    public void objectsShelBeAddedToStores() {
        int subtastStoreSizeExpected = 1;
        int taskStoreSizeExpected = 1;
        int epicStoreSizeExpected = 1;

        assertEquals(subtastStoreSizeExpected, inMemoryTaskManager.getSubtaskStore().size());
        assertEquals(taskStoreSizeExpected, inMemoryTaskManager.getTaskStore().size());
        assertEquals(epicStoreSizeExpected, inMemoryTaskManager.getEpicStore().size());

    }

    @Test
    public void tasksShellBeUpdated() {
        inMemoryTaskManager.updateSubtask(3, "UpdatedSubtask", "NewDescription", Status.DONE);
        inMemoryTaskManager.updateTask(2, "UpdatedTask", "UpdatedTaskDescription", Status.IN_PROGRESS);
        inMemoryTaskManager.updateEpic(1, "UpdatedEpic", "UpdatedEpicDescription");

        assertEquals(3, subtask.getId());
        assertEquals("UpdatedSubtask", subtask.getName());
        assertEquals("NewDescription", subtask.getDescription());
        assertEquals(Status.DONE, subtask.getStatus());
        assertEquals(1, subtask.getEpicID());

        assertEquals(2, task.getId());
        assertEquals("UpdatedTask", task.getName());
        assertEquals("UpdatedTaskDescription", task.getDescription());
        assertEquals(Status.IN_PROGRESS, task.getStatus());

        assertEquals(1, epic.getId());
        assertEquals("UpdatedEpic", epic.getName());
        assertEquals("UpdatedEpicDescription", epic.getDescription());
        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    public void shellAddListOfSubtasks() {
        List<Subtask> subtasks = new ArrayList<>();
        Epic epicForTest = new Epic("Test Epic", "Test Epic DEscr");
        epicForTest.createSubtask("sub1", "des1");
        epicForTest.createSubtask("sub2", "des2");
        epicForTest.createSubtask("sub3", "des3", Status.DONE);

        inMemoryTaskManager.addNewEpic(epicForTest);

        assertEquals(2, inMemoryTaskManager.getEpicStore().size());
        assertEquals(4, inMemoryTaskManager.getSubtaskStore().size());
    }


    @Test
    public void sizeOfStoresShellBe0() {
        inMemoryTaskManager.deleteSubtaskByID(3);
        inMemoryTaskManager.deleteTaskByID(2);
        inMemoryTaskManager.deleteEpicByID(1);

        assertEquals(0, inMemoryTaskManager.getSubtaskStore().size());
        assertEquals(0, inMemoryTaskManager.getTaskStore().size());
        assertEquals(0, inMemoryTaskManager.getEpicStore().size());
    }

    @Test
    public void allSubtasksShellDeletedWhenTheirEpicDeleted() {
        Subtask sub = new Subtask("sub", "des");
        epic.addSubtask(sub);
        inMemoryTaskManager.addNewSubtask(sub);

        assertEquals(2, epic.getSubtaskList().size());
        assertEquals(2, inMemoryTaskManager.getSubtaskStore().size());

        inMemoryTaskManager.deleteEpicByID(1);
        assertEquals(0, inMemoryTaskManager.getSubtaskStore().size());
    }

    @Test
    public void listShellbeTheSameThatEpicsSubtaskList() {
        List<Subtask> listOfEpicsSubs;
        Subtask sub = new Subtask("sub", "des");
        epic.addSubtask(sub);

        listOfEpicsSubs = inMemoryTaskManager.getSubtasksFromEpic(1);

        assertEquals(3, listOfEpicsSubs.getFirst().getId());
        assertEquals("Subtask", listOfEpicsSubs.getFirst().getName());
        assertEquals("Subtask description", listOfEpicsSubs.getFirst().getDescription());
        assertEquals(1, listOfEpicsSubs.getFirst().getEpicID());

        assertEquals(4, listOfEpicsSubs.getLast().getId());
        assertEquals("sub", listOfEpicsSubs.getLast().getName());
        assertEquals("des", listOfEpicsSubs.getLast().getDescription());
        assertEquals(1, listOfEpicsSubs.getLast().getEpicID());

    }

    @Test
    public void gettersTest() {
        Epic testEpic = inMemoryTaskManager.getEpicByID(1);
        Task testTask = inMemoryTaskManager.getTaskByID(2);
        Subtask testSubtask = inMemoryTaskManager.getSubtaskByID(3);

        assertEquals(1, testEpic.getId());
        assertEquals(2, testTask.getId());
        assertEquals(3, testSubtask.getId());
    }

    @Test
    public void allStoresSizeShellBe0() {
        inMemoryTaskManager.clearTaskStore();
        inMemoryTaskManager.clearEpicStore();

        assertEquals(0, inMemoryTaskManager.getTaskStore().size());
        assertEquals(0, inMemoryTaskManager.getEpicStore().size());
        assertEquals(0, inMemoryTaskManager.getSubtaskStore().size());

        Subtask sub = new Subtask("sub", "des");
        inMemoryTaskManager.addNewSubtask(sub);

        assertEquals(1, inMemoryTaskManager.getSubtaskStore().size());
        inMemoryTaskManager.clearSubtaskStore();
        assertEquals(0, inMemoryTaskManager.getSubtaskStore().size());
    }

    @Test
    public void ifCleareSubtaskStoreEpicsShellClearTheirSubtasksList() {
        Subtask sub = new Subtask("sub", "des");
        epic.addSubtask(sub);
        inMemoryTaskManager.addNewSubtask(sub);

        int sizeOfEpicsSubtasksList = epic.getSubtaskList().size();
        int sizeOfSubtaskStore = inMemoryTaskManager.getSubtaskStore().size();
        assertEquals(2, sizeOfEpicsSubtasksList);
        assertEquals(2, sizeOfSubtaskStore);

        inMemoryTaskManager.clearSubtaskStore();
        sizeOfEpicsSubtasksList = epic.getSubtaskList().size();
        sizeOfSubtaskStore = inMemoryTaskManager.getSubtaskStore().size();
        assertEquals(0, sizeOfEpicsSubtasksList);
        assertEquals(0, sizeOfSubtaskStore);
    }

}
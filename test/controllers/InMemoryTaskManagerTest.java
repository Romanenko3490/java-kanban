package controllers;

import exceptions.TimeConflictException;
import models.*;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        inMemoryTaskManager.updateSubtask(3, "UpdatedSubtask", "NewDescription", Status.DONE, "20.05.2025 12:00", 30);
        inMemoryTaskManager.updateTask(2, "UpdatedTask", "UpdatedTaskDescription", Status.IN_PROGRESS, "19.05.2025 11:00", 10);
        inMemoryTaskManager.updateEpic(1, "UpdatedEpic", "UpdatedEpicDescription");

        assertEquals(3, subtask.getId());
        assertEquals("UpdatedSubtask", subtask.getName());
        assertEquals("NewDescription", subtask.getDescription());
        assertEquals(Status.DONE, subtask.getStatus());
        assertEquals(1, subtask.getEpicID());
        assertEquals("20.05.2025 12:00", subtask.getStartTime());
        assertEquals(30, subtask.getDuration().toMinutes());
        assertEquals("20.05.2025 12:30", subtask.getEndTime());

        assertEquals(2, task.getId());
        assertEquals("UpdatedTask", task.getName());
        assertEquals("UpdatedTaskDescription", task.getDescription());
        assertEquals(Status.IN_PROGRESS, task.getStatus());
        assertEquals("19.05.2025 11:00", task.getStartTime());
        assertEquals(10, task.getDuration().toMinutes());
        assertEquals("19.05.2025 11:10", task.getEndTime());

        assertEquals(1, epic.getId());
        assertEquals("UpdatedEpic", epic.getName());
        assertEquals("UpdatedEpicDescription", epic.getDescription());
        assertEquals(Status.DONE, epic.getStatus());
        assertEquals("20.05.2025 12:00", epic.getStartTime());
        assertEquals(30, epic.getDuration().toMinutes());
        assertEquals("20.05.2025 12:30", epic.getEndTime());
    }

    @Test
    public void shellAddListOfSubtasks() {
        List<Subtask> subtasks = new ArrayList<>();
        Epic epicForTest = new Epic("Test Epic", "Test Epic DEscr");
        inMemoryTaskManager.createSubtaskForEpic(epicForTest, "sub1", "des1", "12.04.2024 12:00", 10);
        inMemoryTaskManager.createSubtaskForEpic(epicForTest, "sub2", "des2", "13.04.2024 12:00", 50);
        inMemoryTaskManager.createSubtaskForEpic(epicForTest, "sub3", "des3", "14.04.2024 12:00", 30);

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
        System.out.println(inMemoryTaskManager.getEpicStore());
        System.out.println(inMemoryTaskManager.getSubtaskStore());
        assertEquals(0, inMemoryTaskManager.getSubtaskStore().size());
    }

    @Test
    public void listShellbeTheSameThatEpicsSubtaskList() {
        List<Subtask> listOfEpicsSubs;
        Subtask sub = new Subtask("sub", "des");
        epic.addSubtask(sub);

        listOfEpicsSubs = (List<Subtask>) inMemoryTaskManager.getSubtasksFromEpic(1).get();

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
        Epic testEpic = (Epic) inMemoryTaskManager.getEpicByID(1).get();
        Task testTask = (Task) inMemoryTaskManager.getTaskByID(2).get();
        Subtask testSubtask = (Subtask) inMemoryTaskManager.getSubtaskByID(3).get();

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
    public void ifClearSubtaskStoreEpicsShellClearTheirSubtasksList() {
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

    @Test
    public void testTimeConflictForTasksWithSameTime() {
        Task task1 = new Task("task", "des", "24.10.1991 12:00", 30);
        Task task2 = new Task("task2", "des2", "24.10.1991 12:00", 10);
        Epic epic1 = new Epic("epic", "epic des");

        inMemoryTaskManager.addNewTask(task1);
        inMemoryTaskManager.addNewTask(task2);

        try {
            inMemoryTaskManager.createSubtaskForEpic(epic1, "Sub", "sub des", "24.10.1991 12:00", 30);
        } catch (TimeConflictException e) {

        }

        inMemoryTaskManager.addNewEpic(epic1);

        assertTrue(inMemoryTaskManager.getTaskStore().contains(task1));
        assertFalse(inMemoryTaskManager.getTaskStore().contains(task2));
        if (!epic1.getSubtaskList().isEmpty()) {
            Subtask sub = epic1.getSubtaskList().getFirst();
            assertFalse(inMemoryTaskManager.getSubtaskStore().contains(sub));
        }

        assertTrue(inMemoryTaskManager.getEpicStore().contains(epic1));
    }

    @Test
    public void testTimeConflictForTasksWithCrossingTime() {
        Task task1 = new Task("task", "des", "24.10.1991 12:00", 30);
        Task task2 = new Task("task2", "des2", "24.10.1991 12:29", 10);
        Epic epic1 = new Epic("epic", "epic des");


        inMemoryTaskManager.addNewTask(task1);
        inMemoryTaskManager.addNewTask(task2);
        try {
            inMemoryTaskManager.createSubtaskForEpic(epic1, "Sub", "sub des", "24.10.1991 12:10", 30);
        } catch (TimeConflictException e) {

        }
        inMemoryTaskManager.addNewSubtasks(epic1.getSubtaskList());
        inMemoryTaskManager.addNewEpic(epic1);

        assertTrue(inMemoryTaskManager.getTaskStore().contains(task1));
        assertFalse(inMemoryTaskManager.getTaskStore().contains(task2));
        if (!epic1.getSubtaskList().isEmpty()) {
            assertFalse(inMemoryTaskManager.getSubtaskStore().contains(epic1.getSubtaskList().getFirst()));
        }
        assertTrue(inMemoryTaskManager.getEpicStore().contains(epic1));
    }

    @Test
    public void testTimeConflictForTasksWithContiniousTime() {
        Task task1 = new Task("task", "des", "24.10.1991 12:00", 30);
        Task task2 = new Task("task2", "des2", "24.10.1991 12:30", 10);
        Epic epic1 = new Epic("epic", "epic des");
        inMemoryTaskManager.createSubtaskForEpic(epic1, "Sub", "sub des", "24.10.1991 12:40", 30);


        inMemoryTaskManager.addNewTask(task1);
        inMemoryTaskManager.addNewTask(task2);
        inMemoryTaskManager.addNewEpic(epic1);

        assertTrue(inMemoryTaskManager.getTaskStore().contains(task1));
        assertTrue(inMemoryTaskManager.getTaskStore().contains(task2));
        assertTrue(inMemoryTaskManager.getSubtaskStore().contains(epic1.getSubtaskList().getFirst()));
        assertTrue(inMemoryTaskManager.getEpicStore().contains(epic1));
    }

    @Test
    public void testTimeConflictForTasksWithNonCrossingTime() {
        Task task1 = new Task("task", "des", "24.10.1991 12:00", 30);
        Task task2 = new Task("task2", "des2", "24.10.1991 13:00", 10);
        Epic epic1 = new Epic("epic", "epic des");
        inMemoryTaskManager.createSubtaskForEpic(epic1, "Sub", "sub des", "24.10.1990 12:40", 30);


        inMemoryTaskManager.addNewTask(task1);
        inMemoryTaskManager.addNewTask(task2);
        inMemoryTaskManager.addNewEpic(epic1);

        assertTrue(inMemoryTaskManager.getTaskStore().contains(task1));
        assertTrue(inMemoryTaskManager.getTaskStore().contains(task2));
        assertTrue(inMemoryTaskManager.getSubtaskStore().contains(epic1.getSubtaskList().getFirst()));
        assertTrue(inMemoryTaskManager.getEpicStore().contains(epic1));
    }

    @Test
    public void testTimeConflictForUpdatedTaskIfConflictTaskShellNotBeUpdated() {
        Task task1 = new Task("task", "des", "24.10.1991 12:00", 30);
        Task task2 = new Task("task2", "des2", "24.10.1991 13:00", 10);

        inMemoryTaskManager.addNewTask(task1);
        inMemoryTaskManager.addNewTask(task2);
        inMemoryTaskManager.updateTask(4, "updatedTask", "New Des", Status.NEW, "24.10.1991 13:00", 20);

        Optional<Task> opt = inMemoryTaskManager.getTaskByID(4);
        assertTrue(inMemoryTaskManager.getTaskStore().contains(task1));
        assertTrue(inMemoryTaskManager.getTaskStore().contains(task2));
        assertEquals("task", opt.get().getName());
        assertEquals("des", opt.get().getDescription());
        assertEquals(Status.NEW, opt.get().getStatus());
        assertEquals("24.10.1991 12:00", opt.get().getStartTime());
        assertEquals(30, opt.get().getDuration().toMinutes());
        assertEquals("24.10.1991 12:30", opt.get().getEndTime());
    }

    @Test
    public void testTimeConflictCheckForTasksWithoutTime() {
        Task task1 = new Task("Task 1", "Description");
        Task task2 = new Task("Task 2", "Description");
        Epic epic1 = new Epic("epic", "epic des");
        inMemoryTaskManager.createSubtaskForEpic(epic1, "Sub", "sub des", "24.10.1990 12:40", 30);

        inMemoryTaskManager.addNewTask(task1);
        inMemoryTaskManager.addNewTask(task2);
        inMemoryTaskManager.addNewEpic(epic1);


        assertTrue(inMemoryTaskManager.getTaskStore().contains(task1));
        assertTrue(inMemoryTaskManager.getTaskStore().contains(task2));
        assertTrue(inMemoryTaskManager.getSubtaskStore().contains(epic1.getSubtaskList().getFirst()));
        assertTrue(inMemoryTaskManager.getEpicStore().contains(epic1));

    }

    @Test
    public void shellReturnSortedPrioritySetByDates() {
        Task task1 = new Task("Task 1", "Description", "24.10.1991 11:00", 60);
        Task task2 = new Task("Task 2", "Description", "20.10.1991 12:00", 120);
        Epic epic1 = new Epic("epic", "epic des");

        inMemoryTaskManager.addNewTask(task1);
        inMemoryTaskManager.addNewTask(task2);

        try {
            inMemoryTaskManager.createSubtaskForEpic(epic1, "Sub", "sub des", "24.10.1989 12:40", 30);
        } catch (TimeConflictException e) {

        }

        inMemoryTaskManager.addNewSubtasks(epic1.getSubtaskList());
        inMemoryTaskManager.addNewEpic(epic1);

        ArrayList<AbstractTask> control = new ArrayList<>();
        if (!epic1.getSubtaskList().isEmpty()) {
            control.add(epic1.getSubtaskList().getFirst());
        }

        control.add(task2);
        control.add(task1);
        assertIterableEquals(inMemoryTaskManager.getPrioritizedTasks(), control);
        control.clear();

        try {
            inMemoryTaskManager.updateTask(5, "Tasw", "Description", Status.NEW, "25.10.1991 15:00", 15);
        } catch (TimeConflictException e) {

        }
        if (!epic1.getSubtaskList().isEmpty()) {
            control.add(epic1.getSubtaskList().getFirst());
        }
        control.add(task1);
        control.add(task2);
        assertIterableEquals(inMemoryTaskManager.getPrioritizedTasks(), control);
        control.clear();

        inMemoryTaskManager.clearTaskStore();
        if (!epic1.getSubtaskList().isEmpty()) {
            control.add(epic1.getSubtaskList().getFirst());
        }
        assertIterableEquals(inMemoryTaskManager.getPrioritizedTasks(), control);
        control.clear();

        inMemoryTaskManager.clearEpicStore();

        assertEquals(0, inMemoryTaskManager.getPrioritizedTasks().size());

    }

}
package controllers;

import models.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    Epic epic;
    Task task;
    Subtask subtask;
    InMemoryTaskManager inMemoryTaskManager;

    int epicID;
    int taskID;
    int subId;

    public InMemoryHistoryManagerTest() {
        this.inMemoryTaskManager = new InMemoryTaskManager();
        this.epic = new Epic("Epic", "Epic Description");
        this.task = new Task("Task", "Task Description");
        this.subtask = new Subtask("Subtask", "Subtask description");

        inMemoryTaskManager.addNewSubtask(subtask);
        epic.addSubtask(subtask);
        inMemoryTaskManager.addNewTask(task);
        inMemoryTaskManager.addNewEpic(epic);
        this.epicID = epic.getId();
        this.taskID = task.getId();
        this.subId = subtask.getId();
    }

    @AfterEach
    public void resetIdCounter() {
        AbstractTask.resetIdCounter();
    }

    @Test
    public void methodGeTaskShelPutObjectIHistoryList() {
        int historyListSize = inMemoryTaskManager.getHistoryManager().getHistory().size();
        assertEquals(0, historyListSize);

        inMemoryTaskManager.getEpicByID(1);
        inMemoryTaskManager.getTaskByID(2);
        inMemoryTaskManager.getSubtaskByID(3);

        historyListSize = inMemoryTaskManager.getHistoryManager().getHistory().size();
        assertEquals(3, historyListSize);
    }

    @Test
    public void capacityOfHistoryListShellBe20If20TasksHaveDifferentIDs() {
        int historyListSize = inMemoryTaskManager.getHistoryManager().getHistory().size();
        assertEquals(0, historyListSize);

        for (int i = 0; i < 20; i++) {
            Task task = new Task("task" + i, "des" + i);
            inMemoryTaskManager.addNewTask(task);
            inMemoryTaskManager.getTaskByID(4 + i);
        }

        historyListSize = inMemoryTaskManager.getHistoryManager().getHistory().size();
        assertEquals(20, historyListSize);
    }

    @Test
    public void capacityOfHistoryListShellBe10IfOnly10TasksHaveUniqueIDsWereGot() {
        int historyListSize = inMemoryTaskManager.getHistoryManager().getHistory().size();
        assertEquals(0, historyListSize);

        for (int i = 0; i < 20; i++) {
            Task task = new Task("task" + i, "des" + i);
            inMemoryTaskManager.addNewTask(task);
        }

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                inMemoryTaskManager.getTaskByID(4 + i);
            }
        }

        historyListSize = inMemoryTaskManager.getHistoryManager().getSize();
        assertEquals(10, historyListSize);
    }

    @Test
    public void RemoveSubtaskByManagertShellRemovedFromHistoryAndFromEpicAlsoEpicShellNotContainsIdOfRemovedSub() {
        Subtask subtask2 = new Subtask("subtask2", "subtask2 desc");
        epic.addSubtask(subtask2);
        inMemoryTaskManager.addNewSubtask(subtask2);
        int subtaskID = subtask.getId();
        int sub2ID = subtask2.getId();

        //Проверяем, наличие субтасков в эпики и в менеджере
        assertTrue(epic.getSubtaskList().contains(subtask));
        assertTrue(epic.getSubtaskList().contains(subtask2));
        assertTrue(inMemoryTaskManager.getSubtaskStore().contains(subtask));
        assertTrue(inMemoryTaskManager.getSubtaskStore().contains(subtask2));

        inMemoryTaskManager.getSubtaskByID(subId);
        inMemoryTaskManager.getSubtaskByID(sub2ID);

        //Проверяем наличие суубтасков в истории просмотров
        assertTrue(inMemoryTaskManager.getHistoryManager().getHistory().contains(subtask));
        assertTrue(inMemoryTaskManager.getHistoryManager().getHistory().contains(subtask2));

        inMemoryTaskManager.deleteSubtaskByID(sub2ID);

        assertTrue(epic.getSubtaskList().contains(subtask));
        assertFalse(epic.getSubtaskList().contains(subtask2));

        System.out.println(epic.getSubtaskList());
        System.out.println(inMemoryTaskManager.getSubtaskStore());
        assertTrue(inMemoryTaskManager.getSubtaskStore().contains(subtask));
        assertFalse(inMemoryTaskManager.getSubtaskStore().contains(subtask2));

        assertTrue(inMemoryTaskManager.getHistoryManager().getHistory().contains(subtask));
        assertFalse(inMemoryTaskManager.getHistoryManager().getHistory().contains(subtask2));

    }

    @Test
    public void nodeShellBeRemovedFromHistory() {

        inMemoryTaskManager.getHistoryManager().addToHistory(subtask);
        inMemoryTaskManager.getHistoryManager().addToHistory(epic);
        inMemoryTaskManager.getHistoryManager().addToHistory(task);

        assertTrue(inMemoryTaskManager.getHistoryManager().getHistory().contains(subtask));
        assertTrue(inMemoryTaskManager.getHistoryManager().getHistory().contains(epic));
        assertTrue(inMemoryTaskManager.getHistoryManager().getHistory().contains(task));

        inMemoryTaskManager.getHistoryManager().remove(epicID);
        assertFalse(inMemoryTaskManager.getHistoryManager().getHistory().contains(epic));
        assertTrue(inMemoryTaskManager.getHistoryManager().getHistory().contains(task));
        assertTrue(inMemoryTaskManager.getHistoryManager().getHistory().contains(subtask));

        inMemoryTaskManager.getHistoryManager().remove(subId);
        assertFalse(inMemoryTaskManager.getHistoryManager().getHistory().contains(epic));
        assertTrue(inMemoryTaskManager.getHistoryManager().getHistory().contains(task));
        assertFalse(inMemoryTaskManager.getHistoryManager().getHistory().contains(subtask));


        inMemoryTaskManager.getHistoryManager().remove(taskID);
        assertFalse(inMemoryTaskManager.getHistoryManager().getHistory().contains(epic));
        assertFalse(inMemoryTaskManager.getHistoryManager().getHistory().contains(task));
        assertFalse(inMemoryTaskManager.getHistoryManager().getHistory().contains(subtask));

        assertTrue(inMemoryTaskManager.getHistoryManager().getHistory().isEmpty());

    }
}
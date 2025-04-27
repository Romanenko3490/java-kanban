package models;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    Epic epic = new Epic("Eic1", "Epic Description");


    @AfterEach
    public void resetCounter() {
        AbstractTask.resetIdCounter();
    }


    @Test
    public void shellNotBeNull() {
        assertNotNull(epic);
        assertNotNull(epic.getSubtaskList());
    }

    @Test
    public void checkListOfSubtasks() {
        epic.createSubtask("subtask", "subdescription", "24.10.1991 12:00", 10);

        Subtask subtask = epic.getSubtaskList().getFirst();

        assertEquals("subtask", subtask.getName());
        assertEquals("subdescription", subtask.getDescription());
        assertEquals(2, subtask.getId());
        assertEquals(Status.NEW, subtask.getStatus());
    }

    @Test
    public void statusOfEpicShellChangedWithStatusOfHisSubtasks() {
        assertEquals(Status.NEW, epic.getStatus());

        epic.createSubtask("subtask", "subdescription", "24.10.1992 12:00", 30);
        epic.createSubtask("subtask2", "subdescription2", Status.DONE, "24.10.1993 15:00", 30);

        String subtaskList = epic.getSubtaskList().toString();
        assertEquals(Status.IN_PROGRESS, epic.getStatus(), subtaskList);

        epic.getSubtaskList().getFirst().setStatus(Status.DONE);
        String subtaskListUpdate = epic.getSubtaskList().toString();
        assertEquals(Status.DONE, epic.getStatus(), subtaskListUpdate);
    }

    @Test
    public void subtaskWithId3ShellBeDeleted() {
        epic.createSubtask("subtask", "subdescription", "24.10.1994 12:00", 30);
        epic.createSubtask("subtask2", "subdescription2", Status.DONE, null, 0);

        ArrayList<Subtask> subtasksListForTest = new ArrayList<>();
        subtasksListForTest.add(epic.getSubtaskList().getFirst());

        epic.deleteSubtask(3);
        assertEquals(subtasksListForTest, epic.getSubtaskList());

    }

    @Test
    public void subtasksListSizeShellBe0() {
        epic.createSubtask("subtask", "subdescription", null, 0);
        epic.createSubtask("subtask2", "subdescription2", Status.DONE, "24.04.1099 12:00", 30);

        int subtasklistSize = epic.getSubtaskList().size();

        assertEquals(2, subtasklistSize);

        epic.clearSubtasks();
        subtasklistSize = epic.getSubtaskList().size();
        assertEquals(0, subtasklistSize);
    }

    @Test
    public void testOfEpicStatusBorderCases() {
        Epic epic1 = new Epic("epic 1", "Des 1");
        epic1.createSubtask("Subtask 1", "Description 1", "24.10.2024 12:00", 30);
        epic1.createSubtask("Subtask 2", "Description 2", "24.10.2024 13:00", 30);
        epic1.createSubtask("Subtask 3", "Description 3", "24.10.2024 14:00", 30);

        assertEquals(Status.NEW, epic1.getStatus());

        epic1.getSubtaskList().forEach(subtask -> subtask.setStatus(Status.DONE));
        assertEquals(Status.DONE, epic1.getStatus());

        epic1.getSubtaskList().getFirst().setStatus(Status.NEW);
        assertEquals(Status.IN_PROGRESS, epic1.getStatus());

        epic1.getSubtaskList().forEach(subtask -> subtask.setStatus(Status.IN_PROGRESS));
        assertEquals(Status.IN_PROGRESS, epic1.getStatus());
    }
}
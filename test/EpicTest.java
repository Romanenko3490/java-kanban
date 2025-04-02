import models.AbstractTask;
import models.Epic;
import models.Status;
import models.Subtask;
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
        epic.createSubtask("subtask", "subdescription");

        Subtask subtask = epic.getSubtaskList().getFirst();

        assertEquals("subtask", subtask.getName());
        assertEquals("subdescription", subtask.getDescription());
        assertEquals(2, subtask.getId());
        assertEquals(Status.NEW, subtask.getStatus());
    }

    @Test
    public void statusOfEpicShellChangedWithStatusOfHisSubtasks() {
        assertEquals(Status.NEW, epic.getStatus());

        epic.createSubtask("subtask", "subdescription");
        epic.createSubtask("subtask2", "subdescription2", Status.DONE);

        String subtaskList = epic.getSubtaskList().toString();
        assertEquals(Status.IN_PROGRESS, epic.getStatus(), subtaskList);

        epic.getSubtaskList().getFirst().setStatus(Status.DONE);
        String subtaskListUpdate = epic.getSubtaskList().toString();
        assertEquals(Status.DONE, epic.getStatus(), subtaskListUpdate);
    }

    @Test
    public void subtaskWithId3ShellBeDeleted() {
        epic.createSubtask("subtask", "subdescription");
        epic.createSubtask("subtask2", "subdescription2", Status.DONE);

        ArrayList<Subtask> subtasksListForTest = new ArrayList<>();
        subtasksListForTest.add(epic.getSubtaskList().getFirst());

        epic.deleteSubtask(3);
        assertEquals(subtasksListForTest, epic.getSubtaskList());

    }

    @Test
    public void subtasksListSizeShellBe0() {
        epic.createSubtask("subtask", "subdescription");
        epic.createSubtask("subtask2", "subdescription2", Status.DONE);

        int subtasklistSize = epic.getSubtaskList().size();

        assertEquals(2, subtasklistSize);

        epic.clearSubtasks();
        subtasklistSize = epic.getSubtaskList().size();
        assertEquals(0, subtasklistSize);
    }

    //проверьте, что объект Epic нельзя добавить в самого себя в виде подзадачи;
    //Класс сабтаск не является подклассом епика, не получится в ArrayList<Subtask> subtasks добавить бъект класса Эпик


}
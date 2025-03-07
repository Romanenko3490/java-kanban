import controllers.InMemoryTaskManager;
import models.AbstractTask;
import models.Epic;
import models.Subtask;
import models.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    Epic epic;
    Task task;
    Subtask subtask;
    InMemoryTaskManager inMemoryTaskManager;


    public InMemoryHistoryManagerTest() {
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
    public void capacityOfHistoryListShellNotExceed10Objects() {
        int historyListSize = inMemoryTaskManager.getHistoryManager().getHistory().size();
        assertEquals(0, historyListSize);

        for (int i = 0; i < 20; i++) {
            Task task = new Task("task" + i, "des" + i);
            inMemoryTaskManager.addNewTask(task);
            inMemoryTaskManager.getTaskByID(4 + i);
        }

        historyListSize = inMemoryTaskManager.getHistoryManager().getHistory().size();
        assertEquals(10, historyListSize);
    }


}
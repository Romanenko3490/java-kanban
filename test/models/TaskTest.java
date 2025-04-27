package models;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    Task task1 = new Task("Task1", "Description1");
    Task task2 = new Task("Task2", "Description2", Status.DONE, "11.11.2020 12:00", 30);

    @AfterEach
    public void resetIdCounter() {
        AbstractTask.resetIdCounter();
    }

    @Test
    public void shellNotBeNull() {
        assertNotNull(task1);
        assertNotNull(task2);
    }

    @Test
    public void shellSetupNewNameDescriptionAndStatus() {
        task1.setName("NewName");
        task1.setDescription("NewDescription");
        task1.setStatus(Status.IN_PROGRESS);

        assertEquals("NewName", task1.getName(), "Ошибка имени задачи");
        assertEquals("NewDescription", task1.getDescription(), "Ошибка описания задачи");
        assertEquals(Status.IN_PROGRESS, task1.getStatus(), "Ошибка статуса задачи");
    }

    @Test
    public void shelBeIncrementedPositiveID() {
        assertTrue(task1.getId() > 0);
        assertTrue(task2.getId() > 0);

        assertEquals(1, task1.getId(), task1.getId());
        assertEquals(2, task2.getId(), task2.getId());

    }

    //Проверьте, что экземпляры класса Task равны друг другу, если равен их id;
    @Test
    public void tasksWithSameIdShellBeEqual() {

        assertFalse(task1.getId() == task2.getId());
        assertFalse(task1.equals(task2));

        task2.setId(1);

        assertTrue(task1.getId() == task2.getId());
        assertTrue(task1.equals(task2));
    }

    @Test
    public void checkIdCounter() {
        assertEquals(2, Task.getIdCounter(), Task.getIdCounter());
    }

    @Test
    public void tasksWithSameIdShellHaveSameHascode() {
        task1.setId(2);
        task2.setId(2);
        assertEquals(task1.hashCode(), task2.hashCode());
    }

    @AfterAll
    public static void resetCounterId() {
        AbstractTask.resetIdCounter();
    }

}
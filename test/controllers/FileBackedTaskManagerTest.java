package controllers;

import models.AbstractTask;
import models.Epic;
import models.Subtask;
import models.Task;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

class FileBackedTaskManagerTest {
    FileBackedTaskManager fileBackedTaskManager;
    Epic epic;
    Task task;
    Subtask subtask;
    Path path;

    public FileBackedTaskManagerTest() throws IOException {
        this.path = Files.createTempFile("testSave", ".csv");
        this.fileBackedTaskManager = new FileBackedTaskManager(path);
        this.epic = new Epic("Epic", "Epic Description");
        this.task = new Task("Task", "Task Description");
        this.subtask = new Subtask("Subtask", "Subtask description");
        epic.addSubtask(subtask);

    }

    @AfterEach
    public void resetIdCounter() {
        AbstractTask.resetIdCounter();
    }


    @Test
    public void shellSaveEmptyFile() throws IOException {
        fileBackedTaskManager.save();

        List<String> lines = Files.readAllLines(path);
        assertEquals(1, lines.size());
        assertEquals("id,type,name,status,description,epic", lines.getFirst());
    }

    @Test
    public void testLoadFromEmptyFile() {
        fileBackedTaskManager.save();
        FileBackedTaskManager fm = FileBackedTaskManager.loadFromFile(path);

        assertTrue(fm.getTaskStore().isEmpty());
        assertTrue(fm.getEpicStore().isEmpty());
        assertTrue(fm.getSubtaskStore().isEmpty());
    }

    @Test
    public void shellSaveTasks() throws IOException {
        fileBackedTaskManager.addNewEpic(epic);
        fileBackedTaskManager.addNewTask(task);
        fileBackedTaskManager.addNewSubtask(subtask);

        fileBackedTaskManager.save();

        List<String> lines = Files.readAllLines(path);
        System.out.println(lines);
        assertEquals(4, lines.size());
        assertEquals("id,type,name,status,description,epic", lines.getFirst());
        assertEquals("2,TASK,Task,NEW,Task Description", lines.get(1));
        assertEquals("1,EPIC,Epic,NEW,Epic Description", lines.get(2));
        assertEquals("3,SUBTASK,Subtask,NEW,Subtask description,1", lines.get(3));
    }

    @Test
    public void shellLoadTasksFromFile() {
        fileBackedTaskManager.addNewEpic(epic);
        fileBackedTaskManager.addNewTask(task);
        fileBackedTaskManager.addNewSubtask(subtask);

        fileBackedTaskManager.save();

        FileBackedTaskManager fm = FileBackedTaskManager.loadFromFile(path);

        assertEquals(1, fm.getTaskStore().size());
        assertEquals(1, fm.getEpicStore().size());
        assertEquals(1, fm.getSubtaskStore().size());


        assertTrue(epic.equals(fm.getEpicStore().getFirst()));
        assertTrue(task.equals(fm.getTaskStore().getFirst()));
        assertTrue(subtask.equals(fm.getSubtaskStore().getFirst()));

    }
}
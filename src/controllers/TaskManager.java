package controllers;

import models.*;
import java.util.*;

public class TaskManager {
    private final Map<Integer, Epic> epicStore = new HashMap();
    private final Map<Integer, Task> taskStore = new HashMap<>();
    private final Map<Integer, Subtask> subtaskStore = new HashMap<>();

    //Методы для каждого из типа задач(Задача/Эпик/Подзадача):
    // a. Получение списка всех задач.

    public ArrayList<Epic> getEpicStore() {
        return new ArrayList<>(epicStore.values());
    }

    public ArrayList<Task> getTaskStore() {
        return new ArrayList<>(taskStore.values());
    }

    public ArrayList<Subtask> getSubtaskStore() {
        return new ArrayList<>(subtaskStore.values());
    }

    // b. Удаление всех задач.
    public void clearEpicStore() {
        epicStore.clear();
        subtaskStore.clear();
    }

    public void clearTaskStore() {
        taskStore.clear();
    }

    public void clearSubtaskStore() {
        subtaskStore.clear();
        for (Epic epic : epicStore.values()) {
            epic.clearSubtasks();
        }
    }

    // c. Получение по идентификатору.
    Epic getEpicByID(int id) {
        return epicStore.get(id);
    }

    Task getTaskByID(int id) {
        return taskStore.get(id);
    }

    Subtask getSubtaskByID(int id) {
        return subtaskStore.get(id);
    }

    // d. Создание. Сам объект должен передаваться в качестве параметра.
    public void addEpicToEpicStore(Epic epic) {
       this.epicStore.put(epic.getId(), epic);
    }

    public void addTaskToTaskStore(Task task) {
        this.taskStore.put(task.getId(), task);
    }

    public int addNewTask(Task task) {
        final int id = AbstractTask.getIdCounter() + 1;
        task.setId(id);
        taskStore.put(id, task);
        return id;
    }

    public int addNewSubtask(Subtask subtask) {
        final int id = AbstractTask.getIdCounter() + 1;
        subtask.setId(id);
        subtaskStore.put(id, subtask);
        return id;
    }

    public int addNewEpic(Epic epic) {
        final int id = AbstractTask.getIdCounter() + 1;
        epic.setId(id);
        epicStore.put(id, epic);
        return id;
    }

    public void addSubtaskToSubtaskStore(Subtask subtask) {

        this.subtaskStore.put(subtask.getId(), subtask);
    }

    public void addSubtaskToSubtaskStore(List<Subtask> subtaskList) {
        for (Subtask subtask: subtaskList) {
            subtaskStore.put(subtask.getId(), subtask);
        }
    }
    // e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    void updateEpic(int id, String name, String description) {
        Epic epicToChange = epicStore.get(id);
        epicToChange.setName(name);
        epicToChange.setDescription(description);
        epicToChange.getStatus();

    }

    void updateTask(int id, String name, String description, Status status) {
        Task taskToChange = taskStore.get(id);
        taskToChange.setName(name);
        taskToChange.setDescription(description);
        taskToChange.setStatus(status);
    }

    void updateSubtask(int id, String name, String description, Status status) {
        Subtask subtaskToChange = subtaskStore.get(id);
        subtaskToChange.setName(name);
        subtaskToChange.setDescription(description);
        subtaskToChange.setStatus(status);
    }

    // f. Удаление по идентификатору.
    public void deleteEpicByID(int id) {
        Epic epicToDelete = epicStore.get(id);
        for (Subtask subtask : subtaskStore.values()) {
            if (subtask.getEpicID() == epicToDelete.getId()){
                subtaskStore.remove(subtask);
            }
        }
        epicStore.remove(id);
    }

    public void deleteTaskByID(int id) {

        taskStore.remove(id);
    }

    public void deleteSubtaskByID(int id) {
        Subtask subtaskToDelete = subtaskStore.get(id);
        Epic epicToChange = epicStore.get(subtaskToDelete.getEpicID());
        epicToChange.deleteSubtask(id);
        subtaskStore.remove(id);
    }

    //Получение списка всех подзадач определённого эпика.
    public List<Subtask> getSubtasksFromEpic(int epicID) {
        Epic epic = epicStore.get(epicID);
        return epic.getSubtaskList();
    }


}

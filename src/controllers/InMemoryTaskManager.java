package controllers;

import models.*;
import java.util.*;

public class InMemoryTaskManager <T extends AbstractTask> implements TaskManager {
    private final Map<Integer, Epic> epicStore = new HashMap();
    private final Map<Integer, Task> taskStore = new HashMap<>();
    private final Map<Integer, Subtask> subtaskStore = new HashMap<>();
    //Хранение 10 последних просмотренных задач

    //Методы для каждого из типа задач(Задача/Эпик/Подзадача):
    // a. Получение списка всех задач.

    @Override
    public ArrayList<Epic> getEpicStore() {
        return new ArrayList<>(epicStore.values());
    }

    @Override
    public ArrayList<Task> getTaskStore() {
        return new ArrayList<>(taskStore.values());
    }

    @Override
    public ArrayList<Subtask> getSubtaskStore() {
        return new ArrayList<>(subtaskStore.values());
    }

    // b. Удаление всех задач.
    @Override
    public void clearEpicStore() {
        epicStore.clear();
        subtaskStore.clear();
    }

    @Override
    public void clearTaskStore() {
        taskStore.clear();
    }

    @Override
    public void clearSubtaskStore() {
        subtaskStore.clear();
        for (Epic epic : epicStore.values()) {
            epic.clearSubtasks();
        }
    }

    // c. Получение по идентификатору.
    @Override
    public Epic getEpicByID(int id) {
        InMemoryHistoryManager.addToHistory(epicStore.get(id));
        return epicStore.get(id);
    }

    @Override
    public Task getTaskByID(int id) {
        InMemoryHistoryManager.addToHistory(taskStore.get(id));
        return taskStore.get(id);
    }

    @Override
    public Subtask getSubtaskByID(int id) {
        InMemoryHistoryManager.addToHistory(subtaskStore.get(id));
        return subtaskStore.get(id);
    }

    // d. Создание. Сам объект должен передаваться в качестве параметра.
    @Override
    public void addNewEpic(Epic epic) {
       if (epic.getSubtaskList().size() == 0) {
           this.epicStore.put(epic.getId(), epic);
       } else if (epic.getSubtaskList().size() > 0) {
           List<Subtask> subtasks = epic.getSubtaskList();
           addNewSubtasks(subtasks);
           this.epicStore.put(epic.getId(),epic);
       }
    }

    @Override
    public void addNewTask(Task task) {
        this.taskStore.put(task.getId(), task);
    }

    @Override
    public void addNewSubtask(Subtask subtask) {
        this.subtaskStore.put(subtask.getId(), subtask);
    }

    @Override
    public void addNewSubtasks(List<Subtask> subtaskList) {
        for (Subtask subtask: subtaskList) {
            subtaskStore.put(subtask.getId(), subtask);
        }
    }
    // e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    @Override
    public void updateEpic(int id, String name, String description) {
        Epic epicToChange = epicStore.get(id);
        epicToChange.setName(name);
        epicToChange.setDescription(description);
        epicToChange.getStatus();

    }

    @Override
    public void updateTask(int id, String name, String description, Status status) {
        Task taskToChange = taskStore.get(id);
        taskToChange.setName(name);
        taskToChange.setDescription(description);
        taskToChange.setStatus(status);
    }

    @Override
    public void updateSubtask(int id, String name, String description, Status status) {
        Subtask subtaskToChange = subtaskStore.get(id);
        subtaskToChange.setName(name);
        subtaskToChange.setDescription(description);
        subtaskToChange.setStatus(status);
    }

    // f. Удаление по идентификатору.
    @Override
    public void deleteEpicByID(int id) {
        List<Subtask> subtasksForDelete = new ArrayList<>();
        for (Subtask subtask : subtaskStore.values()) {
            if (subtask.getEpicID() == id){
                subtasksForDelete.add(subtask);
            }
        }
        for (Subtask subtask : subtasksForDelete) {
            subtaskStore.remove(subtask.getId());
        }
        epicStore.remove(id);
    }

    @Override
    public void deleteTaskByID(int id) {

        taskStore.remove(id);
    }

    @Override
    public void deleteSubtaskByID(int id) {
        Subtask subtaskToDelete = subtaskStore.get(id);
        Epic epicToChange = epicStore.get(subtaskToDelete.getEpicID());
        epicToChange.deleteSubtask(id);
        subtaskStore.remove(id);
    }

    //Получение списка всех подзадач определённого эпика.
    @Override
    public List<Subtask> getSubtasksFromEpic(int epicID) {
        Epic epic = epicStore.get(epicID);
        return epic.getSubtaskList();
    }

}

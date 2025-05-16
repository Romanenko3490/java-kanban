package controllers;

import models.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface TaskManager {
    ArrayList<Epic> getEpicStore();

    ArrayList<Task> getTaskStore();

    ArrayList<Subtask> getSubtaskStore();

    // b. Удаление всех задач.
    void clearEpicStore();

    void clearTaskStore();

    void clearSubtaskStore();

    // c. Получение по идентификатору.
    Optional<Epic> getEpicByID(int id);

    Optional<Epic> getEpicByIdAvoidHistory(int id);

    Optional<Task> getTaskByID(int id);

    Optional<Subtask> getSubtaskByID(int id);

    // d. Создание. Сам объект должен передаваться в качестве параметра.
    void addNewEpic(Epic epic);

    void addNewTask(Task task);

    void addNewSubtask(Subtask subtask);

    void addNewSubtasks(List<Subtask> subtaskList);

    // e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    void updateEpic(int id, String name, String description);

    void updateTask(int id, String name, String description, Status status, String startTime, int duration);

    void updateSubtask(int id, String name, String description, Status status, String startTime, int duration);

    // f. Удаление по идентификатору.
    void deleteEpicByID(int id);

    void deleteTaskByID(int id);

    void deleteSubtaskByID(int id);

    //Получение списка всех подзадач определённого эпика.
    Optional<List<Subtask>> getSubtasksFromEpic(int epicID);

    InMemoryHistoryManager getHistoryManager();

    List<AbstractTask> getPrioritizedTasks();

    boolean timeConflictCheck(AbstractTask newTask);

}

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {
    static private final Map<Integer, Epic> epicStore = new HashMap();
    static private final Map<Integer, Task> taskStore = new HashMap<>();
    static private final Map<Integer, Epic.Subtask> subtaskStore = new HashMap<>();

    //Методы для каждого из типа задач(Задача/Эпик/Подзадача):
    // a. Получение списка всех задач.

    public Map<Integer, Epic> getEpicStore() {
        return epicStore;
    }

    public Map<Integer, Task> getTaskStore() {
        return taskStore;
    }

    public Map<Integer, Epic.Subtask> getSubtaskStore() {
        return subtaskStore;
    }

    // b. Удаление всех задач.
    public void clearEpicStore() {
        epicStore.clear();
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

    Epic.Subtask getSubtaskByID(int id) {
        return subtaskStore.get(id);
    }

    // d. Создание. Сам объект должен передаваться в качестве параметра.
    static void addEpicToEpicStore(Epic epic) {
        epicStore.put(epic.getId(), epic);
    }

    static void addTaskToTaskStore(Task task) {
        taskStore.put(task.getId(), task);
    }

    static void addSubtaskToSubtaskStore(Epic.Subtask subtask) {
        subtaskStore.put(subtask.getId(), subtask);
    }
    // e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    void updateEpic(int id, String name, String description) {
        Epic epicToChange = epicStore.get(id);
        epicToChange.setName(name);
        epicToChange.setDescription(description);
        epicToChange.updateStatus();

    }

    void updateTask(int id, String name, String description, String status) {
        Task taskToChange = taskStore.get(id);
        taskToChange.setName(name);
        taskToChange.setDescription(description);
        taskToChange.setStatus(status);
    }

    void updateSubtask(int id, String name, String description, String status) {
        Epic.Subtask subtaskToChange = subtaskStore.get(id);
        subtaskToChange.setName(name);
        subtaskToChange.setDescription(description);
        subtaskToChange.setStatus(status);
    }

    // f. Удаление по идентификатору.
    void deleteEpicByID(int id) {
        Epic epicToDelete = epicStore.get(id);
        for (Epic.Subtask subtask : subtaskStore.values()) {
            if (subtask.getEpicID() == epicToDelete.getId()){
                subtaskStore.remove(subtask);
            }
        }
        epicStore.remove(id);
    }

    void deleteTaskByID(int id) {
        taskStore.remove(id);
    }

    void deleteSubtaskByID(int id) {
        Epic.Subtask subtaskToDelete = subtaskStore.get(id);
        Epic epicToChange = epicStore.get(subtaskToDelete.getEpicID());
        epicToChange.deleteSubtask(id);
        subtaskStore.remove(id);
    }

    //Получение списка всех подзадач определённого эпика.
    List<Epic.Subtask> getSubtasksFromEpic(int epicID) {
        Epic epic = epicStore.get(epicID);
        return epic.getSubtaskList();
    }
//утилитарыне удалить


}

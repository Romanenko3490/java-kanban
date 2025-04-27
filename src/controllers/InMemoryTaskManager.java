package controllers;

import utility.Managers;
import models.*;
import exceptions.TimeConflictException;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Stream;

public class InMemoryTaskManager<T extends AbstractTask> implements TaskManager {
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private final Map<Integer, Epic> epicStore = new HashMap();
    private final Map<Integer, Task> taskStore = new HashMap<>();
    private final Map<Integer, Subtask> subtaskStore = new HashMap<>();

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
        epicStore.keySet().forEach(historyManager::remove);
        epicStore.clear();
        subtaskStore.keySet().forEach(historyManager::remove);
        subtaskStore.clear();
    }

    @Override
    public void clearTaskStore() {
        taskStore.keySet().forEach(historyManager::remove);
        taskStore.clear();
    }

    @Override
    public void clearSubtaskStore() {

        epicStore.values().forEach(Epic::clearSubtasks);
        subtaskStore.keySet().forEach(historyManager::remove);

        subtaskStore.clear();
    }

    // c. Получение по идентификатору.
    @Override
    public Optional<Epic> getEpicByID(int id) {
        return Optional.ofNullable(epicStore.get(id))
                .map(epic -> {
                    getHistoryManager().addToHistory(epic);
                    return epic;
                });
    }

    @Override
    public Optional<Task> getTaskByID(int id) {
        return Optional.ofNullable(taskStore.get(id))
                .map(task -> {
                    getHistoryManager().addToHistory(task);
                    return task;
                });
    }

    @Override
    public Optional<Subtask> getSubtaskByID(int id) {
        return Optional.ofNullable(subtaskStore.get(id))
                .map(subtask -> {
                    getHistoryManager().addToHistory(subtask);
                    return subtask;
                });
    }

    // d. Создание. Сам объект должен передаваться в качестве параметра.
    @Override
    public void addNewEpic(Epic epic) {
        try {
            if (timeConflictCheck(epic)) {
                throw new TimeConflictException("Эпик c id " + epic.getId() + " не была добавлена - пересекается во времени с существующими");
            }

            if (epic.getSubtaskList().size() == 0) {
                this.epicStore.put(epic.getId(), epic);
            } else if (epic.getSubtaskList().size() > 0) {
                epic.getSubtaskList().forEach(this::addNewSubtask);
                this.epicStore.put(epic.getId(), epic);
            }
        } catch (TimeConflictException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void addNewTask(Task task) {
        try {
            if (timeConflictCheck(task)) {
                throw new TimeConflictException("Задача c id " + task.getId() + " не была добавлена - пересекается во времени с существующими");
            }
            this.taskStore.put(task.getId(), task);
        } catch (TimeConflictException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void addNewSubtask(Subtask subtask) {
        try {
            if (timeConflictCheck(subtask))
                throw new TimeConflictException("Подзадача " + subtask.getId() + "не была добавлена - пересекается во времени с существующими");
            this.subtaskStore.put(subtask.getId(), subtask);
        } catch (TimeConflictException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void addNewSubtasks(List<Subtask> subtaskList) {
        try {
            subtaskList.forEach(subtask -> {
                if (timeConflictCheck(subtask))
                    throw new TimeConflictException("Подзадача " + subtask.getId() + "не была добавлена - пересекается во времени с существующими");
                subtaskStore.put(subtask.getId(), subtask);
            });

        } catch (TimeConflictException e) {
            System.out.println(e.getMessage());
        }
    }

    // e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    @Override
    public void updateEpic(int id, String name, String description) {
        Optional.ofNullable(epicStore.get(id))
                .ifPresent(epic -> {
                    Epic epicToChange = epicStore.get(id);
                    epicToChange.setName(name);
                    epicToChange.setDescription(description);
                    epicToChange.getStatus();
                });

    }

    //Не уверен, что тут есть смысл переписывать через функциональный стиль
    @Override
    public void updateTask(int id, String name, String description, Status status, String startTime, int duration) {
        try {
            Task taskToChange = taskStore.get(id);
            if (taskToChange == null) {
                System.out.println("Задачи с " + id + " не существует");
                return;
            }

            Task tempTask = new Task(id, name, description, status, startTime, duration);

            if (timeConflictCheck(tempTask)) {
                throw new TimeConflictException("Задача " + id + "не была добавлена - пересекается во времени с существующими");
            }

            taskToChange.setName(name);
            taskToChange.setDescription(description);
            taskToChange.setStatus(status);
            taskToChange.setStartTime(startTime);
            taskToChange.setDuration(duration);
//
//            if (timeConflictCheck(taskToChange))
//                throw new TimeConflictException("Задача " + id + "не была добавлена - пересекается во времени с существующими");
//            else {
//                taskToChange.setName(name);
//                taskToChange.setDescription(description);
//                taskToChange.setStatus(status);
//                taskToChange.setStartTime(startTime);
//                taskToChange.setDuration(duration);
//            }
//
        } catch (TimeConflictException e) {
            System.out.println(e.getMessage());
        } catch (NullPointerException e) {
            System.out.println("Задачи с id " + id + "не существует");
            System.out.println(e.getMessage());
        }
    }

    //и тут. Думаю будет менее четабельно.
    @Override
    public void updateSubtask(int id, String name, String description, Status status, String startTime, int duration) {
        try {
            Subtask subtaskToChange = subtaskStore.get(id);
            subtaskToChange.setName(name);
            subtaskToChange.setDescription(description);
            subtaskToChange.setStatus(status);
            subtaskToChange.setStartTime(startTime);
            subtaskToChange.setDuration(duration);
            if (timeConflictCheck(subtaskToChange))
                throw new TimeConflictException("Подзадача " + id + "не была добавлена - пересекается во времени с существующими");
        } catch (TimeConflictException e) {
            System.out.println(e.getMessage());
        } catch (NullPointerException e) {
            System.out.println("Подзадачи с id " + id + "не существует");
            System.out.println(e.getMessage());
        }
    }

    // f. Удаление по идентификатору.
    @Override
    public void deleteEpicByID(int id) {
        List<Subtask> subtasksForDelete = new ArrayList<>();
        for (Subtask subtask : subtaskStore.values()) {
            if (subtask.getEpicID() == id) {
                subtasksForDelete.add(subtask);
            }
        }

        for (Subtask subtask : subtasksForDelete) {
            subtaskStore.remove(subtask.getId());
            historyManager.remove(subtask.getId());
        }
        //Хотел написать так, но тут придеться два раза пройтись по коллекциям... Не уверен, что будет эффективнее исходника
//        subtaskStore.values().stream()
//                .filter(subtask -> subtask.getEpicID() == id)
//                .map(subtask -> subtask.getId())
//                .forEach(historyManager::remove);
//        subtaskStore.values().removeIf(subtask -> subtask.getEpicID() == id);


        epicStore.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteTaskByID(int id) {
        taskStore.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteSubtaskByID(int id) {
        Optional.ofNullable(subtaskStore.get(id))
                .ifPresent(subtask -> {
                    Optional.ofNullable(epicStore.get(subtask.getEpicID()))
                            .ifPresent(epic -> epic.deleteSubtask(id));
                    subtaskStore.remove(id);
                    historyManager.remove(id);
                });
    }

    //Получение списка всех подзадач определённого эпика.
    @Override
    public Optional<List<Subtask>> getSubtasksFromEpic(int epicID) {
        Epic epic = epicStore.get(epicID);
        return epic != null ? Optional.of(epic.getSubtaskList()) : Optional.empty();
    }

    //добавил метод, чтоб возвращать свой хисторименеджер длятестов
    public InMemoryHistoryManager getHistoryManager() {
        return (InMemoryHistoryManager) historyManager;
    }


    //В задании не понятно, нужно ли помещать суда эпики
    //Распределение задач по приоретету
    public Set<AbstractTask> getPrioritizedTasks() {
        Set<AbstractTask> tasksByPriority = new TreeSet<>(Comparator
                .comparing(task -> LocalDateTime.parse(task.getStartTime(), task.getFormatter())));

        getTaskStore().stream()
                .filter(task -> task.getStartTime() != null)
                //.filter(task -> LocalDateTime.parse(task.getStartTime(), task.getFormatter()) != null)
                .forEach(tasksByPriority::add);

        getSubtaskStore().stream()
                .filter(stask -> stask.getStartTime() != null)
                //.filter(stask -> LocalDateTime.parse(stask.getStartTime(), stask.getFormatter()) != null)
                .forEach(tasksByPriority::add);

        return tasksByPriority;
    }

    private boolean isTimeOverlap(AbstractTask task1, AbstractTask task2) {
        if (task1.getStartTime() == null || task1.getDuration() == null ||
                task2.getStartTime() == null || task2.getDuration() == null)
            return false;

        try {
            LocalDateTime startTime1 = LocalDateTime.parse(task1.getStartTime(), task1.getFormatter());
            LocalDateTime startTime2 = LocalDateTime.parse(task2.getStartTime(), task2.getFormatter());

            LocalDateTime endTime1 = LocalDateTime.parse(task1.getEndTime(), task1.getFormatter());
            LocalDateTime endTime2 = LocalDateTime.parse(task2.getEndTime(), task2.getFormatter());

            return startTime1.isBefore(endTime2) && startTime2.isBefore(endTime1);

        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private boolean timeConflictCheck(AbstractTask newTask) {
        if (newTask.getStartTime() == null || newTask.getDuration() == null)
            return false;

        return Stream.concat(taskStore.values().stream(), subtaskStore.values().stream())
                .filter(task -> !task.equals(newTask))
                .filter(task -> task.getStartTime() != null && task.getDuration() != null)
                .anyMatch(task -> isTimeOverlap(task, newTask));

    }

}

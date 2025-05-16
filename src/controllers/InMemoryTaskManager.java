package controllers;

import utility.Managers;
import models.*;
import exceptions.TimeConflictException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Stream;

public class InMemoryTaskManager<T extends AbstractTask> implements TaskManager {
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private final Map<Integer, Epic> epicStore = new HashMap();
    private final Map<Integer, Task> taskStore = new HashMap<>();
    private final Map<Integer, Subtask> subtaskStore = new HashMap<>();
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    protected final Set<AbstractTask> prioritizedTasks = new TreeSet<>(Comparator.comparing(AbstractTask::getStartTimeInFormat));

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
        epicStore.values().stream()
                .forEach(epic -> {
                    epic.getSubtaskList().stream()
                            .filter(subtask -> subtask.getStartTime() != null)
                            .forEach(prioritizedTasks::remove);
                });

        epicStore.keySet().forEach(historyManager::remove);
        epicStore.clear();
        subtaskStore.keySet().forEach(historyManager::remove);
        subtaskStore.clear();
    }

    @Override
    public void clearTaskStore() {
        taskStore.values().stream()
                .filter(task -> task.getStartTime() != null)
                .forEach(prioritizedTasks::remove);

        taskStore.keySet().forEach(historyManager::remove);
        taskStore.clear();
    }

    @Override
    public void clearSubtaskStore() {
        subtaskStore.values().stream()
                .filter(subtask -> subtask.getStartTime() != null)
                .forEach(prioritizedTasks::remove);

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

    //Пришлось дописать такой метод, так как в СабтаскХендлере при добавлении сабтаска в эпик вызывается метод
    //getEpicByID() чтобы в него добавить сабтаск, и получается что эпик залетает в историю просмотров.
    //к сожалению не додумался, как по другому избежать добавления в историю
    //еще был вариант добавить флажок в метод getEpicByID(int id, boolean addToHistoryOrNot), но решил от него отказаться
    public Optional<Epic> getEpicByIdAvoidHistory(int id) {
        return Optional.ofNullable(epicStore.get(id));
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
        //При добавлении эпика, содержащего подзадачи, я также добавляю и его подзадачи и проверяю их на временном отрезке
        //в том смысле, что если эпик уже содержет подзадачи, перед добавлением.
        //просто если пользлваться только методом таскменеджера, то этот мeтод будет прост,
        //а каждую задачу придется добавлять дополнительным методом.
        if (epic != null)
            this.epicStore.put(epic.getId(), epic);
//        try {
//            if (timeConflictCheck(epic)) {
//                throw new TimeConflictException("Эпик c id " + epic.getId() + " не была добавлена - пересекается во времени с существующими");
//            }
//
//            if (epic.getSubtaskList().size() == 0) {
//                this.epicStore.put(epic.getId(), epic);
//            } else if (epic.getSubtaskList().size() > 0) {
//                epic.getSubtaskList().forEach(subtask -> subtaskStore.put(subtask.getId(), subtask));
//                epic.getSubtaskList().stream()
//                        .filter(task -> task.getStartTime() != null)
//                        .forEach(prioritizedTasks::add);
//                this.epicStore.put(epic.getId(), epic);
//            }
//        } catch (TimeConflictException e) {
//            System.out.println(e.getMessage());
//        }
    }

    @Override
    public void addNewTask(Task task) {
        try {
            if (timeConflictCheck(task)) {
                throw new TimeConflictException("Задача c id " + task.getId() + " не была добавлена - пересекается во времени с существующими");
            }
            if (task.getStartTime() != null) {
                this.prioritizedTasks.add(task);
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
            if (subtask.getStartTime() != null) {
                this.prioritizedTasks.add(subtask);
            }
        } catch (TimeConflictException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void addNewSubtasks(List<Subtask> subtaskList) {
        if (subtaskList == null) return;

        subtaskList.forEach(subtask -> {
            try {
                if (timeConflictCheck(subtask)) {
                    throw new TimeConflictException("Подзадача " + subtask.getId() + " не была добавлена - пересекается во времени с существующими");
                }
                subtaskStore.put(subtask.getId(), subtask);
                if (subtask.getStartTime() != null) {
                    this.prioritizedTasks.add(subtask);
                }
            } catch (TimeConflictException e) {
                System.out.println(e.getMessage());
            }
        });
    }

    //Добавил метод, чтобы е=при TimeConflict сабтаск не добавлялся в эпик
    public Subtask createSubtaskForEpic(Epic epic, String name, String description, String startTime, int duration) {
        Subtask subtask = new Subtask(name, description, startTime, duration);

        if (timeConflictCheck(subtask)) {
            throw new TimeConflictException("Подзадача " + subtask.getId() + " не была добавлена - пересекается во времени с существующими");
        }
        subtask.setEpicID(epic.getId());
        epic.addSubtask(subtask);
        subtaskStore.put(subtask.getId(), subtask);
        return subtask;
    }

    public Subtask createSubtaskForEpic(Epic epic, String name, String description) {
        Subtask subtask = new Subtask(name, description);
        subtask.setEpicID(epic.getId());
        epic.addSubtask(subtask);
        subtaskStore.put(subtask.getId(), subtask);
        return subtask;
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
            if (taskToChange.getStartTime() != null) {
                prioritizedTasks.remove(taskToChange);
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

            if (taskToChange.getStartTime() != null) {
                prioritizedTasks.add(taskToChange);
            }

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
            if (subtaskToChange == null) {
                System.out.println("Задачи с " + id + " не существует");
                return;
            }
            prioritizedTasks.remove(subtaskToChange);

            Subtask tempSubTask = new Subtask(id, name, description, status, startTime, duration, subtaskToChange.getEpicID());
            if (timeConflictCheck(tempSubTask)) {
                throw new TimeConflictException("Подзадача с id " + id + " не была добавлена - пересекается во времени с существующими");
            }

            subtaskToChange.setName(name);
            subtaskToChange.setDescription(description);
            subtaskToChange.setStatus(status);
            subtaskToChange.setStartTime(startTime);
            subtaskToChange.setDuration(duration);
            prioritizedTasks.add(subtaskToChange);

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
        Optional.ofNullable(epicStore.get(id))
                .ifPresentOrElse(epic -> {
                    List<Subtask> subsToDelete = subtaskStore.values().stream()
                            .filter(subtask -> subtask.getEpicID() == id)
                            .toList();

                    subsToDelete.stream()
                            .filter(subtask -> subtask.getStartTime() != null)
                            .forEach(prioritizedTasks::remove);

                    subsToDelete.stream()
                            .forEach(subtask -> {
                                historyManager.remove(subtask.getId());
                                subtaskStore.remove(subtask.getId());
                            });
                    epicStore.remove(id);
                    historyManager.remove(id);

                }, () -> System.out.println("Эпик с id " + id + " не найден."));

    }

    @Override
    public void deleteTaskByID(int id) {
        Task task = taskStore.get(id);
        if (task != null) {
            try {

                if (task.getStartTime() != null) {
                    prioritizedTasks.remove(task);
                }
                taskStore.remove(id);
                historyManager.remove(id);
            } catch (Exception e) {
                System.err.println("Ошибка удаления task ID " + id + ": " + e.getMessage());
            }
        }
    }

    @Override
    public void deleteSubtaskByID(int id) {
        Subtask subtask = subtaskStore.get(id);
        if (subtask == null) {
            System.out.println("Подзадачи с id " + id + " не найдено.");
            return;
        }

        try {
            Epic epic = epicStore.get(subtask.getEpicID());
            if (epic != null) {
                epic.deleteSubtask(id);
            } else {
                System.out.println("Эпика с id " + subtask.getEpicID() + " не найдено.");
            }

            if (subtask.getStartTime() != null) {
                prioritizedTasks.remove(subtask);
            }

            subtaskStore.remove(id);
            if (historyManager != null) {
                historyManager.remove(id);
            }
        } catch (Exception e) {
            System.err.println("Ошибка при удалении подзадачи " + id + ": " + e.getMessage());
            e.printStackTrace();
        }
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


    public List<AbstractTask> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
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

    public boolean timeConflictCheck(AbstractTask newTask) {
        if (newTask.getStartTime() == null || newTask.getDuration() == null)
            return false;

        return Stream.concat(taskStore.values().stream(), subtaskStore.values().stream())
                .filter(task -> !task.equals(newTask))
                .filter(task -> task.getStartTime() != null && task.getDuration() != null)
                .anyMatch(task -> isTimeOverlap(task, newTask));

    }


}

package controllers;

import exceptions.ManagerSaveException;
import models.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private Path path;

    public FileBackedTaskManager(Path path) {
        if (Files.exists(path)) {
            this.path = path;
        } else {
            try {
                Files.createFile(path);
                System.out.println("Файл сохранения успешно создан: " + path.toAbsolutePath());
            } catch (IOException e) {
                System.out.println("Произошла ошибка при создании файла сохранения");
                e.printStackTrace();
            }
        }
    }

    @Override
    public void clearEpicStore() {
        super.clearEpicStore();
        save();
    }

    @Override
    public void clearTaskStore() {
        super.clearTaskStore();
        save();
    }

    @Override
    public void clearSubtaskStore() {
        super.clearSubtaskStore();
        save();
    }

    @Override
    public void addNewEpic(Epic epic) {
        super.addNewEpic(epic);
        save();
    }

    @Override
    public void addNewTask(Task task) {
        super.addNewTask(task);
        save();
    }

    @Override
    public void addNewSubtask(Subtask subtask) {
        super.addNewSubtask(subtask);
        save();
    }

    @Override
    public void addNewSubtasks(List list) {
        super.addNewSubtasks(list);
        save();
    }

    @Override
    public void updateEpic(int id, String name, String description) {
        super.updateEpic(id, name, description);
        save();
    }

    @Override
    public void updateTask(int id, String name, String description, Status status, String startTime, int durationInMin) {
        super.updateTask(id, name, description, status, startTime, durationInMin);

        save();
    }

    @Override
    public void updateSubtask(int id, String name, String description, Status status, String startTime, int durationInMin) {
        super.updateSubtask(id, name, description, status, startTime, durationInMin);
        save();
    }

    @Override
    public void deleteEpicByID(int id) {
        super.deleteEpicByID(id);
        save();
    }

    @Override
    public void deleteTaskByID(int id) {
        super.deleteTaskByID(id);
        save();
    }

    @Override
    public void deleteSubtaskByID(int id) {
        super.deleteSubtaskByID(id);
        save();
    }

    public void save() {
        try (Writer writer = new FileWriter(String.valueOf(path))) {
            writer.write("id,type,name,status,description,start time, duration in min, epic id \n");
            ArrayList<Task> tasks = getTaskStore();
            tasks.stream().map(Task::stringForSerialize).forEach(s -> {
                try {
                    writer.write(s + "\n");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            ArrayList<Epic> epics = getEpicStore();
            epics.stream().map(Epic::stringForSerialize).forEach(s -> {
                try {
                    writer.write(s + "\n");
                } catch (IOException e) {
                    throw new RuntimeException();
                }
            });


            ArrayList<Subtask> subtasks = getSubtaskStore();
            subtasks.stream().map(Subtask::stringForSerialize).forEach(s -> {
                try {
                    writer.write(s + "\n");
                } catch (IOException e) {
                    throw new RuntimeException();
                }
            });


        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка сохранения", e);
        }
    }

    public static FileBackedTaskManager loadFromFile(Path path) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(path);
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(path.toFile()))) {
            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();
                String[] split = line.split(",");
                if (line.startsWith("id,type")) {
                    continue;
                }
                try {
                    Integer id = Integer.parseInt(split[0]);
                    String name = split[2];
                    String description = split[4];
                    Status status = checkStatus(split[3]);
                    String startTime = checkStartTime(split[5]);
                    Integer durationInMin = Integer.parseInt(split[6]);

                    if (split[1].equals(Types.TASK.toString())) {
                        Task task = new Task(id, name, description, status, startTime, durationInMin);
                        fileBackedTaskManager.addNewTask(task);
                    } else if (split[1].equals(Types.EPIC.toString())) {
                        Epic epic = new Epic(id, name, description, status, startTime, durationInMin);
                        fileBackedTaskManager.addNewEpic(epic);
                    } else if (split[1].equals(Types.SUBTASK.toString())) {
                        Integer epicID = Integer.parseInt(split[7]);
                        Subtask subtask = new Subtask(id, name, description, status, startTime, durationInMin, epicID);
                        Epic epicToAddSubtask = (Epic) fileBackedTaskManager.getEpicByID(epicID).get();
                        epicToAddSubtask.addSubtask(subtask);
                        fileBackedTaskManager.addNewSubtask(subtask);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Ошибка парсинга в строке " + line);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка загрузки", e);
        }
        return fileBackedTaskManager;
    }

    private static Status checkStatus(String string) {
        if (string.equals("NEW")) {
            return Status.NEW;
        } else if (string.equals("IN_PROGRESS")) {
            return Status.IN_PROGRESS;
        } else return Status.DONE;
    }

    private static String checkStartTime(String str) {
        if (str.equals("null")) return null;
        return str;
    }

}


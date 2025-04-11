package controllers;

import exceptions.ManagerSaveException;
import models.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    Path path;

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
    public void updateTask(int id, String name, String description, Status status) {
        super.updateTask(id, name, description, status);
        save();
    }

    @Override
    public void updateSubtask(int id, String name, String description, Status status) {
        super.updateSubtask(id, name, description, status);
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
            writer.write("id,type,name,status,description,epic\n");
            ArrayList<Task> tasks = getTaskStore();
            if (!tasks.isEmpty()) {
                for (Task task : tasks) {
                    writer.write(task.stringForSerialize() + "\n");
                }
            }
            ArrayList<Epic> epics = getEpicStore();
            if (!epics.isEmpty()) {
                for (Epic epic : epics) {
                    writer.write(epic.stringForSerialize() + "\n");
                }
            }
            ArrayList<Subtask> subtasks = getSubtaskStore();
            if (!subtasks.isEmpty()) {
                for (Subtask subtask : subtasks) {
                    writer.write(subtask.stringForSerialize() + "\n");
                }
            }


        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка сохранения", e);
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.out.println("Некорректные данные");
            e.printStackTrace();
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

                    if (split[1].equals(Types.TASK.toString())) {
                        Task task = new Task(id, name, description, status);
                        fileBackedTaskManager.addNewTask(task);
                    } else if (split[1].equals(Types.EPIC.toString())) {
                        Epic epic = new Epic(id, name, description, status);
                        fileBackedTaskManager.addNewEpic(epic);
                    } else if (split[1].equals(Types.SUBTASK.toString())) {
                        Integer epicID = Integer.parseInt(split[5]);
                        Subtask subtask = new Subtask(id, name, description, status, epicID);
                        Epic epicToAddSubtask = fileBackedTaskManager.getEpicByID(epicID);
                        epicToAddSubtask.addSubtask(subtask);
                        fileBackedTaskManager.addNewSubtask(subtask);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Ошибка парсинга в строке " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла" + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("Ошибка при обработке данных");
        }
        updateIdCounter(path);
        return fileBackedTaskManager;
    }

    private static Status checkStatus(String string) {
        if (string.equals("NEW")) {
            return Status.NEW;
        } else if (string.equals("IN_PROGRESS")) {
            return Status.IN_PROGRESS;
        } else
            return Status.DONE;
    }


    //Написал метод для обнавления idCounter, почему-то и без него айди получался нормальный,
    //но мне кажется могут быть конфликты при загрузке, хотя у меня не получилось их смоделировать.
    //Может быть этот метод лишний.
    private static void updateIdCounter(Path path) {
        int maxId = 0;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(path.toFile()));
            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();
                if (line.startsWith("id,type"))
                    continue;
                String[] split = line.split(",");
                int currentID = Integer.parseInt(split[0]);
                if (currentID > maxId) {
                    maxId = currentID;
                }

            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        AbstractTask.setIdCounter(maxId + 1);
    }
}


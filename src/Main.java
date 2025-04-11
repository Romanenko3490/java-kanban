import models.*;
import controllers.*;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) {
        //сохранение
        Task task1 = new Task("Task 1", "Description task 1");
        Task task2 = new Task("Task 2", "Description task 2");
        Epic epic1 = new Epic("epic 1", "decription epic 1");
        epic1.createSubtask("subtask 1", "Description subtask 1");
        epic1.createSubtask("subtask 2", "description subtask 2");
        epic1.createSubtask("subtask 3", "desceiption subtask 3");
        Epic epic2 = new Epic("Epic 2", "Description Epic 2");

        Path path = Paths.get("save.csv");
        FileBackedTaskManager fm = new FileBackedTaskManager(path);
        fm.addNewTask(task1);
        fm.addNewTask(task2);
        fm.addNewEpic(epic1);
        fm.addNewSubtasks(epic1.getSubtaskList());

        fm.addNewEpic(epic2);
        fm.deleteEpicByID(7);
        fm.deleteTaskByID(2);

        // загрузка
//        Path path = Paths.get("save.csv");
//        FileBackedTaskManager fm = FileBackedTaskManager.loadFromFile(path);
//
//        System.out.println(fm.getTaskStore());
//        System.out.println();
//        System.out.println(fm.getEpicStore());
//        System.out.println();
//        System.out.println(fm.getSubtaskStore());
//
//        Task task = new Task("task new", "des new");
//        fm.addNewTask(task);
//        //System.out.println(fm.getTaskStore());
//
//        Epic epic = new Epic("epic new", "dwe");
//        fm.addNewEpic(epic);
//        System.out.println();
//        System.out.println(fm.getTaskStore());
//        System.out.println();
//        System.out.println(fm.getEpicStore());


    }

}

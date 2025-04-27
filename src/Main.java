import models.*;
import controllers.*;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) {
//        Task task = new Task("task 1", "desc", "01.01.1801 15:00", 60); // 1
//        System.out.println(task.getStartTime());
//        System.out.println(task.getDuration().toMinutes());
//        System.out.println(task.getEndTime());
//        System.out.println();
//        Subtask subtask = new Subtask("subtask", "desc", "24.10.1991 13:50", 50); //2
//        System.out.println(subtask.getStartTime());
//        System.out.println(subtask.getEndTime());
//        System.out.println(subtask.getDuration().toMinutes());
//
//        Epic epic = new Epic("epic", "desc"); //3
//        epic.addSubtask(subtask);
//        System.out.println("epic");
//        System.out.println(epic.getDuration().toMinutes());
//        Subtask subtask1 = new Subtask("s", "des", "24.10.1991 12:00", 120); //4
//        epic.addSubtask(subtask1);
//        System.out.println("Epic after subtask 2 : " + epic.getDuration().toMinutes());
//        System.out.println();
//        System.out.println(epic.getStartTime());
//        Subtask subtask2 = new Subtask("sub", "de");
//        epic.addSubtask(subtask2);
//        System.out.println();
//        System.out.println(epic.getStartTime());
//        System.out.println(epic.getEndTime());
//        System.out.println();
//        System.out.println("epic 2");
//        Epic epic2 = new Epic("epic2", "des");
//        System.out.println(epic2.getStartTime());
//        System.out.println(epic2.getDuration().toMinutes());
//        System.out.println(epic2.getEndTime());
//        FileBackedTaskManager manager = new FileBackedTaskManager(Paths.get("src//resources//save.csv"));
//        System.out.println("\n Приорити \n");
////        manager.addNewSubtask(subtask);
////        manager.addNewSubtask(subtask1);
////        manager.addNewSubtask(subtask2);
//        manager.addNewTask(task);
//        manager.addNewEpic(epic);
//        manager.addNewEpic(epic2);
//
//        System.out.println(manager.getSubtaskStore());
//        System.out.println();
//        System.out.println();
//        System.out.println(manager.getPrioritizedTasks());
//
//
//        System.out.println();
//        System.out.println("epic subtasks");
//        System.out.println(epic.getSubtaskList());
//        System.out.println();
//        epic.deleteSubtask(2);
//        System.out.println(epic.getSubtaskList());
//
//
//        System.out.println();
//        System.out.println("addnewEpic");
//
//        Epic epic3 = new Epic("asda", "asdasd");
//        epic3.createSubtask("asd", "asda", "24.04.2025 19:00", 50);
//        epic3.createSubtask("q121", "2e", "24.04.2025 20:00", 60);
//        System.out.println(epic3);
//        manager.addNewEpic(epic3);
//        System.out.println();
//        System.out.println(manager.getSubtaskStore());
//
//
//        manager.updateTask(9999999, "asda", "asd", Status.NEW, "24.10.2025", 25);
//
//        manager.deleteSubtaskByID(9999);
//
//        System.out.println(manager.getPrioritizedTasks());
//        manager.getTaskByID(1);
//        manager.getSubtaskByID(8);
//        manager.getSubtaskByID(9);
//
//        System.out.println();
//        System.out.println("history");
//        System.out.println();
//        System.out.println(manager.getHistoryManager().getHistory());

        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(Paths.get("src//resources//save.csv"));

        System.out.println("субтаск");
        System.out.println(manager.getSubtaskStore());
        System.out.println();
        System.out.println("task");
        System.out.println(manager.getTaskStore());
        System.out.println();
        System.out.println("epic");
        System.out.println(manager.getEpicStore());

    }

}

import models.*;
import controllers.*;

import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) {
//        FileBackedTaskManager manager = new FileBackedTaskManager(Paths.get("src//resources//save.csv"));
//        Task taskId1 = new Task("tast1", "Des1", "24.10.1991 12:00", 30);
//        Task taskId2 = new Task("task2", "des");
//
//        Epic epicId3 = new Epic("epic1", "des");
//        manager.createSubtaskForEpic(epicId3, "subId4", "des", "24.10.1991 12:00", 10);
//        manager.createSubtaskForEpic(epicId3,"subId5", "des");
//
//        Epic epicId6 = new Epic("epic2", "des");
//        manager.createSubtaskForEpic(epicId6,"subID7", "des", "24.10.1990 13:00", 120);
//        manager.createSubtaskForEpic(epicId6,"subtId8", "des", "24.10.1991 15:00", 60);
//
//        manager.addNewTask(taskId1);
//        manager.addNewTask(taskId2);
//        manager.addNewEpic(epicId3);
//        manager.addNewSubtasks(epicId3.getSubtaskList());
//        manager.addNewEpic(epicId6);
//        manager.addNewSubtasks(epicId6.getSubtaskList());
//       // System.out.println(manager.getPrioritizedTasks());
//
//        manager.updateTask(2, "task2", "desc2", Status.NEW, "24.10.2005 12:00", 30);
//        System.out.println();
//        //System.out.println(manager.getPrioritizedTasks());
//
//        System.out.println();
//        //System.out.println(manager.getPrioritizedTasks());
//        //manager.clearTaskStore();
//        //System.out.println(manager.getPrioritizedTasks());
//        //System.out.println(manager.getEpicStore());
//        manager.deleteEpicByID(3);
//        System.out.println(manager.getEpicStore());
//        System.out.println();
//        System.out.println(manager.getSubtaskStore());





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

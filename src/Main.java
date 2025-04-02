import models.*;
import controllers.*;

public class Main {

    public static void main(String[] args) {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        Task task1 = new Task("Task 1", "Description task 1");
        Task task2 = new Task("Task 2", "Description task 2");
        Epic epic1 = new Epic("epic 1", "decription epic 1");
        epic1.createSubtask("subtask 1", "Description subtask 1");
        epic1.createSubtask("subtask 2", "description subtask 2");
        epic1.createSubtask("subtask 3", "desceiption subtask 3");
        Epic epic2 = new Epic("Epic 2", "Description Epic 2");
        inMemoryTaskManager.addNewTask(task1);
        inMemoryTaskManager.addNewTask(task2);
        inMemoryTaskManager.addNewEpic(epic1);
        inMemoryTaskManager.addNewEpic(epic2);
        inMemoryTaskManager.addNewSubtasks(epic1.getSubtaskList());

        inMemoryTaskManager.getTaskByID(2);
        inMemoryTaskManager.getEpicByID(7);
        inMemoryTaskManager.getSubtaskByID(5);
        inMemoryTaskManager.getSubtaskByID(4);
        inMemoryTaskManager.getEpicByID(7);
        inMemoryTaskManager.getEpicByID(3);
        inMemoryTaskManager.getEpicByID(7);
        inMemoryTaskManager.getEpicByID(7);
        inMemoryTaskManager.getEpicByID(7);
        inMemoryTaskManager.getTaskByID(2);
        System.out.println(inMemoryTaskManager.getHistoryManager().getHistory());
        System.out.println(inMemoryTaskManager.getHistoryManager().getSize());
        //вызывов гетера 10 раз, уникальных айди 5, сайз двусвязного списка - 5

        inMemoryTaskManager.deleteTaskByID(2);
        System.out.println();
        System.out.println();
        System.out.println(inMemoryTaskManager.getHistoryManager().getHistory());
        inMemoryTaskManager.deleteSubtaskByID(6);
        System.out.println();
        System.out.println();
        System.out.println(inMemoryTaskManager.getHistoryManager().getHistory());
        inMemoryTaskManager.clearTaskStore();
        System.out.println();
        System.out.println();
        System.out.println(inMemoryTaskManager.getHistoryManager().getHistory());
        inMemoryTaskManager.deleteEpicByID(3);
        System.out.println();
        System.out.println();
        System.out.println(inMemoryTaskManager.getHistoryManager().getHistory());
        System.out.println(inMemoryTaskManager.getHistoryManager().getSize());
        // в истории остался только один эпик йди - 7, длинна двусвязного списка - 1

    }

}

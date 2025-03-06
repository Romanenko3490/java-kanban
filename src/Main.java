import models.*;
import controllers.*;

public class Main {

    public static void main(String[] args) {
        //Создайте две задачи, а также эпик с двумя подзадачами и эпик с одной подзадачей
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
//        Task task1 = new Task("I'm Task1", "description of task 1");
//        inMemoryTaskManager.addNewTask(task1);
//        Task task2 = new Task("I'm Task2", "description of task 2");
//        inMemoryTaskManager.addNewTask(task2);
//        Epic epic1 = new Epic("I'm Epic1", "Description of epic 1");
//        inMemoryTaskManager.addNewEpic(epic1);
//        epic1.createSubtask("I'm subtask 1","description of subtask 1");
//        epic1.createSubtask("I'm subtask 2", "description subtask 2");
//        inMemoryTaskManager.addNewSubtasks(epic1.getSubtaskList());
//        Epic epic2 = new Epic("I'm Epic2", "description of epic 2");
//        inMemoryTaskManager.addNewEpic(epic2);
//        epic2.createSubtask("I'm sbtask 3", "Description of subtask 3");
//        inMemoryTaskManager.addNewSubtasks(epic2.getSubtaskList());
//
//        System.out.println("Распечатайте списки эпиков, задач и подзадач");
//        System.out.println(inMemoryTaskManager.getEpicStore() + "\n");
//        System.out.println(inMemoryTaskManager.getTaskStore() + "\n");
//        System.out.println(inMemoryTaskManager.getSubtaskStore());
//        System.out.println("_____________________________________________" + "\n");
//
//        System.out.println("Измените статусы созданных объектов, распечатайте их.");
//        System.out.println("Проверьте, что статус задачи и подзадачи сохранился, а статус эпика рассчитался по статусам подзадач.");
//        task1.setStatus(Status.IN_PROGRESS);
//        task2.setStatus(Status.DONE);
//        epic1.getSubtaskList().getFirst().setStatus(Status.IN_PROGRESS);
//        epic1.getSubtaskList().getLast().setStatus(Status.DONE);
//        epic2.getSubtaskList().getFirst().setStatus(Status.DONE);
//
//        System.out.println(inMemoryTaskManager.getTaskStore() + "\n");
//        System.out.println(inMemoryTaskManager.getEpicStore() + "\n");
//        System.out.println(inMemoryTaskManager.getSubtaskStore());
//        System.out.println("_____________________________________________" + "\n");
//
//        System.out.println("И, наконец, попробуйте удалить одну из задач и один из эпиков.");
//        inMemoryTaskManager.deleteTaskByID(2);
//        inMemoryTaskManager.deleteEpicByID(3);
//        System.out.println(inMemoryTaskManager.getTaskStore() + "\n");
//        System.out.println(inMemoryTaskManager.getEpicStore() + "\n");
//        System.out.println("_____________________________________________" + "\n");

//      Тесты истории
        System.out.println(InMemoryHistoryManager.getHistory());

        for (int i = 1; i < 13; i++) {
            Task task = new Task("Задача " + i, "дескрипшен " + i);
            inMemoryTaskManager.addNewTask(task);
        }
        System.out.println(inMemoryTaskManager.getTaskStore());

        for (int i = 1; i < 13; i++) {
            inMemoryTaskManager.getTaskByID(i);
        }
        System.out.println();
        System.out.println(InMemoryHistoryManager.getHistory());

        Epic epic = new Epic("epic 1", "description epic");
        epic.createSubtask("sub1", "des1");
        epic.createSubtask("sub2", "des2");
        inMemoryTaskManager.addNewEpic(epic);
        inMemoryTaskManager.addNewSubtasks(epic.getSubtaskList());
        inMemoryTaskManager.getEpicByID(13);
        inMemoryTaskManager.getSubtaskByID(14);
        inMemoryTaskManager.getSubtaskByID(15);
        System.out.println();
        System.out.println(InMemoryHistoryManager.getHistory());
    }

}

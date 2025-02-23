import models.*;
import controllers.*;

public class Main {

    public static void main(String[] args) {
        //Создайте две задачи, а также эпик с двумя подзадачами и эпик с одной подзадачей
        TaskManager taskManager = new TaskManager();
        Task task1 = new Task("I'm Task1", "description of task 1");
        taskManager.addTaskToTaskStore(task1);
        Task task2 = new Task("I'm Task2", "description of task 2");
        taskManager.addTaskToTaskStore(task2);
        Epic epic1 = new Epic("I'm Epic1", "Description of epic 1");
        taskManager.addEpicToEpicStore(epic1);
        epic1.createSubtask("I'm subtask 1","description of subtask 1");
        epic1.createSubtask("I'm subtask 2", "description subtask 2");
        // так как поля таскменеджера нужно было сделать не статическими и менеджеров предпологается несколько,
        // перегрузил метод addSubtaskToSubtaskStore, чтобы была возможность добавить сразу весь список подзадач епика одним методом
        taskManager.addSubtaskToSubtaskStore(epic1.getSubtaskList());
        Epic epic2 = new Epic("I'm Epic2", "description of epic 2");
        taskManager.addEpicToEpicStore(epic2);
        epic2.createSubtask("I'm sbtask 3", "Description of subtask 3");
        taskManager.addSubtaskToSubtaskStore(epic2.getSubtaskList());

        System.out.println("Распечатайте списки эпиков, задач и подзадач");
        System.out.println(taskManager.getEpicStore() + "\n");
        System.out.println(taskManager.getTaskStore() + "\n");
        System.out.println(taskManager.getSubtaskStore());
        System.out.println("_____________________________________________" + "\n");

        System.out.println("Измените статусы созданных объектов, распечатайте их.");
        System.out.println("Проверьте, что статус задачи и подзадачи сохранился, а статус эпика рассчитался по статусам подзадач.");
        task1.setStatus(Status.IN_PROGRESS);
        task2.setStatus(Status.DONE);
        epic1.getSubtaskList().getFirst().setStatus(Status.IN_PROGRESS);
        epic1.getSubtaskList().getLast().setStatus(Status.DONE);
        epic2.getSubtaskList().getFirst().setStatus(Status.DONE);

        System.out.println(taskManager.getTaskStore() + "\n");
        System.out.println(taskManager.getEpicStore() + "\n");
        System.out.println(taskManager.getSubtaskStore());
        System.out.println("_____________________________________________" + "\n");

        System.out.println("И, наконец, попробуйте удалить одну из задач и один из эпиков.");
        taskManager.deleteTaskByID(2);
        taskManager.deleteEpicByID(3);
        System.out.println(taskManager.getTaskStore() + "\n");
        System.out.println(taskManager.getEpicStore() + "\n");
        System.out.println("_____________________________________________" + "\n");

    }
}

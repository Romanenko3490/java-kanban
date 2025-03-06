package Utility;

import controllers.HistoryManager;
import controllers.InMemoryHistoryManager;
import controllers.InMemoryTaskManager;
import controllers.TaskManager;

public class Managers <T extends TaskManager> {

    //Managers должен сам подбирать нужную реализацию TaskManager и возвращать объект правильного типа.
    // Со временем будем много реализаций интерфейса TaskManager, но так как она пока одна, реализовал метод так

    public TaskManager getDefault() {
        TaskManager manager = new InMemoryTaskManager<>();
        return manager;
    }

    public HistoryManager getDefaultHistory() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        return historyManager;
    }
}

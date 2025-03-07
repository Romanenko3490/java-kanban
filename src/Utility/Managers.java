package Utility;

import controllers.HistoryManager;
import controllers.InMemoryHistoryManager;
import controllers.InMemoryTaskManager;
import controllers.TaskManager;

public class Managers {

    //Да, наверное вы правы, обобщение тут ни к чему, если будет множество разных менеджеров.

    //Возможно наворотил бутерброд какой-то тут =)
    public TaskManager getDefault() {
        TaskManager manager = new InMemoryTaskManager<>();
        return manager;
    }

    public static HistoryManager getDefaultHistory() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        return historyManager;
    }
}

package controllers;

import models.AbstractTask;
import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    public static ArrayList<AbstractTask> historyList = new ArrayList<>(10);

    //Получение истории просмотров, получение последних 10 просмотренных задач
    public static ArrayList<AbstractTask> getHistory() {
        return historyList;
    }

    //Логика добавления задачи в список истории просмотра
    public static void addToHistory(AbstractTask task) {
        if (historyList.size() < 10) {
            historyList.add(task);
        } else if (historyList.size() == 10) {
            historyList.removeFirst();
            historyList.add(task);
        }
    }

    public static void clearHistory() {
        historyList.clear();
    }

}

package controllers;

import models.AbstractTask;
import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private ArrayList<AbstractTask> historyList = new ArrayList<>(10);

    //Получение истории просмотров, получение последних 10 просмотренных задач
    @Override
    public ArrayList<AbstractTask> getHistory() {
        ArrayList<AbstractTask> listForReturn = new ArrayList<>(historyList);
        return listForReturn;
    }

    //Логика добавления задачи в список истории просмотра
    //спасибо за подсказку!
    @Override
    public void addToHistory(AbstractTask task) {
        if (task == null)
            return;
        historyList.add(task);
        if (historyList.size() > 10)
            historyList.removeFirst();
    }

    public void clearHistory() {
        historyList.clear();
    }

}

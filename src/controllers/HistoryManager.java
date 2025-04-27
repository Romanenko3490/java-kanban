package controllers;

import models.AbstractTask;
import models.Subtask;

import java.util.ArrayList;

public interface HistoryManager<T extends AbstractTask> {

    //Получение истории просмотров, получение последних 10 просмотренных задач
    ArrayList<AbstractTask> getHistory();

    //Логика добавления задачи в список истории просмотра. Назвал метод не add как в задании, а то не понятно куда add
    void addToHistory(AbstractTask task);

    void remove(int id);

}

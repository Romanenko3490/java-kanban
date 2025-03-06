package controllers;

import models.AbstractTask;
import java.util.ArrayList;

public interface HistoryManager <T extends AbstractTask> {
    ArrayList<AbstractTask> historyList = new ArrayList<>();


    //Сделал методы статическими, чтобы в классе inMemoryTaskManager при вызове getTask можно было обратиться к методу
    //addToHistory. Ну и вообще в моем понимании, объектов истории просмотров не может быть много. Поэтому
    // принял решение, что как методы так и поле хранящее историю просмотров должно быть статическим.
    //Получение истории просмотров, получение последних 10 просмотренных задач
    static ArrayList<AbstractTask> getHistory() {
        return historyList;
    }

    //Логика добавления задачи в список истории просмотра. Назвал метод не add как в задании, а то не понятно куда add
    static void addToHistory(AbstractTask task){
    }


}

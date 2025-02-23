package models;

import java.util.ArrayList;
import java.util.List;

public class Epic extends AbstractTask {
    private final List<Subtask> subtaskList = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    private void setStatus() {
        int newCount = 0;
        int completedCount = 0;
        for (Subtask subtask : subtaskList) {
            if (subtask.getStatus().equals(Status.NEW))
                newCount++;
            if (subtask.getStatus().equals(Status.DONE)) {
                completedCount++;
            }
        }
        if (subtaskList.isEmpty() || newCount == subtaskList.size()) {
            this.status = Status.NEW;
        } else if (completedCount == subtaskList.size()) {
            this.status = Status.DONE;
        } else
            this.status = Status.IN_PROGRESS;
    }


    public Status getStatus() {
        setStatus();
        //Оставил реализацию метода, преддложеного вами, во время теста отображается не верный статус эпика.
        // Вдруг решите посмотреть =). Также в Main закоментил условия, при которых тестировал. Спасибо
        //epicCheckStatus(this);
        return status;
    }

    private void addSubtask(Subtask subtask) {
        subtaskList.add(subtask);
        subtask.setEpicID(getId());
    }

    public List<Subtask> getSubtaskList() {
        return subtaskList;
    }

    public void createSubtask(String name, String description) {
        Subtask subtask = new Subtask(name, description);
        subtaskList.add(subtask);
        subtask.setEpicID(getId());
    }

    public void createSubtask(String name, String description, Status status) {
        Subtask subtask = new Subtask(name, description, status);
        subtaskList.add(subtask);
        subtask.setEpicID(getId());
    }

    public void clearSubtasks() {
        subtaskList.clear();
    }

    public void deleteSubtask(int id) {
        for (Subtask subtask : subtaskList) {
            if (subtask.getId() == id) {
                subtaskList.remove(subtask);
            }
        }
    }


    @Override
    public String toString() {
        return "Epic{ ID='" + getId() + "', name='" + getName() + "', description='" + getDescription() +
                "', status='" + getStatus() +"', contains subtasks:\n" + getSubtaskList();
    }


    // Проверил предложеный вами алгоритм, он логичен и короток, но возникает ситуация, когда подзадача имеет статус
    // DONE, а другие NEW, тогда статус эпика будет DONE, что не верно. Думал думал, как доработать, чтоб было коротко,
    // но не приходит на ум алгоритм без использования каунтера выполненых подзадач =(
    public void epicCheckStatus(Epic epic) {
        int newStat = 0;
        for (Subtask subtask : epic.getSubtaskList()) {
            if (subtask.getStatus() == Status.IN_PROGRESS) {
                setStatus(Status.IN_PROGRESS);
                return;
            }
            if (subtask.getStatus() == Status.NEW) {
                newStat++;
            }
        }
        if (newStat == subtaskList.size()) {
            setStatus(Status.NEW);
        } else {
            setStatus(Status.DONE);
        }
    }
}


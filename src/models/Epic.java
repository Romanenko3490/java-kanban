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
        return status;
    }

    public void addSubtask(Subtask subtask) {
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
        Subtask subtaskToDelete = null;
        for (Subtask subtask : subtaskList) {
            if (subtask.getId() == id) {
                subtaskToDelete = subtask;
            }
        }
        subtaskList.remove(subtaskToDelete);
    }


    @Override
    public String toString() {
        return "Epic{ ID='" + getId() + "', name='" + getName() + "', description='" + getDescription() +
                "', status='" + getStatus() + "', contains subtasks:\n" + getSubtaskList();
    }


}


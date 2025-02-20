import java.util.ArrayList;
import java.util.List;

public class Epic extends AbstractTask{
    Status status;
    private final List<Subtask> subtaskList = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
        updateStatus();
        TaskManager.addEpicToEpicStore(this);
    }

    void updateStatus() {
        boolean isNew = true;
        boolean isCompleted = false;
        int newCount = 0;
        int completedCount = 0;
        for (Subtask subtask : subtaskList) {
            if (subtask.getStatus().equals(Status.NEW))
                newCount++;
            if (subtask.getStatus().equals(Status.DONE)) {
                completedCount++;
            }
        }
        if (newCount != subtaskList.size())
            isNew = false;
        if (completedCount == subtaskList.size())
            isCompleted = true;

        if (subtaskList.isEmpty() || isNew) {
            this.status = Status.NEW;
        } else if (isCompleted) {
            this.status = Status.DONE;
        } else
            this.status = Status.IN_PROGRESS;
    }

    Status getStatus() {
        updateStatus();
        return status;
    }



    private void addSubtask(Subtask subtask) {
        subtaskList.add(subtask);
    }

    List<Subtask> getSubtaskList() {
        return subtaskList;
    }

    void createSubtask(String name, String description) {
        Subtask subtask = new Subtask(name, description);
        subtaskList.add(subtask);
    }

    void createSubtask(String name, String description, String status) {
        Subtask subtask = new Subtask(name, description, status);
        subtaskList.add(subtask);
    }

    void clearSubtasks() {
        subtaskList.clear();
    }

    void deleteSubtask(int id) {
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

    class Subtask extends AbstractTask {
        Status status;

        public Subtask(String name, String description) {
            super(name, description);
            status = Status.NEW;
            TaskManager.addSubtaskToSubtaskStore(this);
        }

        public Subtask(String name, String description, String status) {
            super(name, description);
            setStatus(status);
            TaskManager.addSubtaskToSubtaskStore(this);
        }

        void setStatus(String newStatus) {
            if (newStatus.equalsIgnoreCase("new")) {
                this.status = Status.NEW;
            } else if (newStatus.equalsIgnoreCase("in progress") ||
                    newStatus.equalsIgnoreCase("in_progress")) {
                this.status = Status.IN_PROGRESS;
            } else if (newStatus.equalsIgnoreCase("done")) {
                this.status = Status.DONE;
            }
        }

        void setStatus(Status newStatus) {
            this.status = newStatus;
        }



        Status getStatus() {
            return status;
        }

        int getEpicID () {
            return Epic.this.getId();
        }


        @Override
        public String toString() {
            return "Subtask{ ID='" + getId() + "' name='" + getName() + "', description='" + getDescription() + "' " +
                    "status='" + getStatus()+ "' belongs to epic ID='" + getEpicID() +"'\n";
        }
    }




}

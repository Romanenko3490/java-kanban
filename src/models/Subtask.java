package models;

public class Subtask extends AbstractTask {
    private int epicID;

    public Subtask(String name, String description) {
        super(name, description);
    }

    public Subtask(String name, String description, Status status) {
        super(name, description);
        this.status = status;
    }

    public void setStatus(Status newStatus) {
        this.status = newStatus;
    }

    public void setEpicID(int epicID) {
        this.epicID = epicID;
    }

    public int getEpicID() {
        return epicID;
    }


    @Override
    public String toString() {
        return "Subtask{ ID='" + getId() + "' name='" + getName() + "', description='" + getDescription() + "' " +
                "status='" + getStatus() + "' belongs to epic ID='" + getEpicID() + "'\n";
    }
}


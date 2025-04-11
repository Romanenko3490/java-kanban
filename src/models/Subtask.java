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

    public Subtask(Integer id, String name, String description, Status status, Integer epicID) {
        super(name, description);
        this.status = status;
        setId(id);
        this.epicID = epicID;
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


    public String stringForSerialize() {
        return getId() + "," + Types.SUBTASK + "," + getName() + "," +
                getStatus() + "," + getDescription() + "," + getEpicID();
    }

    @Override
    public String toString() {
        return "Subtask{ ID='" + getId() + "' name='" + getName() + "', description='" + getDescription() + "' " +
                "status='" + getStatus() + "' belongs to epic ID='" + getEpicID() + "'\n";
    }
}


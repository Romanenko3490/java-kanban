package models;


public class Subtask extends AbstractTask {
    private int epicID;

    public Subtask(String name, String description) {
        super(name, description);
    }

    public Subtask(String name, String description, String startTime, int durationInMin) {
        super(name, description);
        setDuration(durationInMin);
        setStartTime(startTime);
    }

    public Subtask(String name, String description, Status status, String startTime, int durationInMin) {
        super(name, description);
        this.status = status;
        setDuration(durationInMin);
        setStartTime(startTime);
    }

    public Subtask(Integer id, String name, String description, Status status, String startTime, int durationInMin, Integer epicID) {
        super(name, description);
        this.status = status;
        setId(id);
        this.epicID = epicID;
        setDuration(durationInMin);
        setStartTime(startTime);
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
                getStatus() + "," + getDescription() + "," + getStartTime() + "," + getDuration().toMinutes() + "," + getEpicID() + ",}";
    }

    @Override
    public String toString() {
        return "Subtask{ ID='" + getId() + "' name='" + getName() + "', description='" + getDescription() + "' " +
                "status='" + getStatus() + "' ST= " + getStartTime() + "' Dur='" + getDuration().toMinutes() + "min' ET=" +
                getEndTime() + "' belongs to epic ID='" + getEpicID() + "\n";
    }
}


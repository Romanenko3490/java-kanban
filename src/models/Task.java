package models;


public class Task extends AbstractTask {

    public Task(String name, String description) {
        super(name, description);
    }

    public Task(String name, String description, String startTime, int durationInMin) {
        super(name, description);
        setDuration(durationInMin);
        setStartTime(startTime);
    }

    public Task(String name, String description, Status status, String startTime, int durationInMin) {
        super(name, description);
        this.status = status;
        setDuration(durationInMin);
        setStartTime(startTime);
    }

    public Task(Integer id, String name, String description, Status status, String startTime, int durationInMin) {
        super(name, description);
        this.status = status;
        this.setId(id);
        setDuration(durationInMin);
        setStartTime(startTime);
    }


    public String stringForSerialize() {
        return getId() + "," + Types.TASK + "," + getName() + "," +
                getStatus() + "," + getDescription() + "," + getStartTime() + "," + getDuration().toMinutes() + ",}";
    }

    @Override
    public String toString() {
        return "Task{ ID='" + getId() + "' name='" + getName() + "', description='" + getDescription() + "' " +
                "status='" + getStatus() + "' ST= " + getStartTime() + "' Dur='" + getDuration().toMinutes() + "min' ET=" +
                getEndTime() + "'";
    }

}


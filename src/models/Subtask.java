package models;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends AbstractTask {
    private int epicID;
    private Duration duration;
    LocalDateTime startTime;

    public Subtask(String name, String description) {
        super(name, description);
    }

    public Subtask(String name, String description, String startTime, int durationInMin) {
        super(name, description);
        this.duration = Duration.ofMinutes(durationInMin);
        this.startTime = startTime != null ? LocalDateTime.parse(startTime, getFormatter()) : null;
    }

    public Subtask(String name, String description, Status status, String startTime, int durationInMin) {
        super(name, description);
        this.status = status;
        this.duration = Duration.ofMinutes(durationInMin);
        this.startTime = startTime != null ? LocalDateTime.parse(startTime, getFormatter()) : null;
    }

    public Subtask(Integer id, String name, String description, Status status, String startTime, int durationInMin, Integer epicID) {
        super(name, description);
        this.status = status;
        setId(id);
        this.epicID = epicID;
        this.duration = Duration.ofMinutes(durationInMin);
        this.startTime = startTime != null ? LocalDateTime.parse(startTime, getFormatter()) : null;
    }

    public void setStatus(Status newStatus) {
        this.status = newStatus;
    }

    public void setStartTime(String startTime) {
        this.startTime = LocalDateTime.parse(startTime, getFormatter());
    }


    @Override
    public String getStartTime() {
        return startTime != null ? startTime.format(getFormatter()) : null;
    }

    public void setDuration(int durationInMin) {
        this.duration = Duration.ofMinutes(durationInMin);
    }

    @Override
    public Duration getDuration() {
        return duration != null ? duration : Duration.ZERO;
    }

    @Override
    public String getEndTime() {
        if (getStartTime() != null) {
            LocalDateTime endTime = LocalDateTime.parse(getStartTime(), getFormatter()).plusMinutes(duration.toMinutes());
            return endTime.format(getFormatter());
        } else
            return null;

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


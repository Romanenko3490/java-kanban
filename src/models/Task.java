package models;

import java.time.Duration;
import java.time.LocalDateTime;

public class Task extends AbstractTask {
    Duration duration;
    LocalDateTime startTime;

    public Task(String name, String description) {
        super(name, description);
    }

    public Task(String name, String description, String startTime, int durationInMin) {
        super(name, description);
        this.duration = Duration.ofMinutes(durationInMin);
        this.startTime = startTime != null ? LocalDateTime.parse(startTime, getFormatter()) : null;
    }

    public Task(String name, String description, Status status, String startTime, int durationInMin) {
        super(name, description);
        this.status = status;
        this.duration = Duration.ofMinutes(durationInMin);
        this.startTime = startTime != null ? LocalDateTime.parse(startTime, getFormatter()) : null;
    }

    public Task(Integer id, String name, String description, Status status, String startTime, int durationInMin) {
        super(name, description);
        this.status = status;
        this.setId(id);
        this.duration = Duration.ofMinutes(durationInMin);
        this.startTime = startTime != null ? LocalDateTime.parse(startTime, getFormatter()) : null;
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


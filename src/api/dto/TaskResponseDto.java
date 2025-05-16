package api.dto;

import models.Status;

import java.time.Duration;

public class TaskResponseDto {
    private int id;
    private String name;
    private String description;
    private String status;
    private String startTime;
    private Long durationMinutes;
    private String endTime;

    // Конструктор
    public TaskResponseDto(int id, String name, String description,
                           String status, String startTime,
                           Long durationMinutes, String endTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.durationMinutes = durationMinutes;
        this.endTime = endTime;
    }

    public TaskResponseDto(int id, String name, String description, Status status, String startTime, Duration duration, String endTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status.toString();
        this.startTime = startTime;
        this.durationMinutes = duration.toMinutes();
        this.endTime = endTime;
    }

    public TaskResponseDto(int id, String name, String description, Status status, String startTime, Long duration, String endTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status.toString();
        this.startTime = startTime;
        this.durationMinutes = duration;
        this.endTime = endTime;
    }

    public TaskResponseDto(int id, String name, String description, Status status, String startTime, int duration, String endTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status.toString();
        this.startTime = startTime;
        this.durationMinutes = (long) duration;
        this.endTime = endTime;
    }

    public TaskResponseDto(int id, String name, String description, String status, String startTime, Duration duration, String endTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status.toString();
        this.startTime = startTime;
        this.durationMinutes = duration.toMinutes();
        this.endTime = endTime;
    }

    // Геттеры
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public Status getStatusInFormat() {
        if (status.equals("NEW"))
            return Status.NEW;
        if (status.equals("DONE"))
            return Status.DONE;
        return Status.IN_PROGRESS;
    }

    public String getStartTime() {
        return startTime;
    }

    public Long getDurationMinutes() {
        return durationMinutes;
    }

    public String getEndTime() {
        return endTime;
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setStatus(Status status) {
        if (status.equals(Status.NEW))
            this.status = "NEW";
        if (status.equals(Status.DONE))
            this.status = "DONE";
        if (status.equals(Status.IN_PROGRESS))
            this.status = "IN_PROGRESS";

    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setDurationMinutes(long durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return id + " " + name + " " + " " + description + " " + status + " " + startTime + " " + durationMinutes + " " + endTime;
    }
}
package models;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public abstract class AbstractTask {
    private static int idCounter = 1;
    private int id;
    private String name;
    private String description;
    protected Status status;
    private Duration duration;
    private LocalDateTime startTime;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");


    public AbstractTask(String name, String description) {
        this.name = name;
        this.description = description;
        this.id = idCounter;
        idCounter++;
        this.status = Status.NEW;
    }

    // Гетеры
    public int getId() {
        return this.id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public static int getIdCounter() {
        return idCounter;
    }

    //Доабвил метод для более удобного тестирования
    public static void resetIdCounter() {
        idCounter = 1;
    }

    //сеттеры
    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static void setIdCounter(int newIdCounter) {
        idCounter = newIdCounter;
    }


    public String getStartTime() {
        return this.startTime != null ? startTime.format(getFormatter()) : null;
    }

    public LocalDateTime getStartTimeInFormat() {
        return this.startTime;
    }

    public Duration getDuration() {
        return duration != null ? duration : Duration.ZERO;
    }


    public void setDuration(int durationInMin) {
        this.duration = Duration.ofMinutes(durationInMin);
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime != null ? LocalDateTime.parse(startTime, getFormatter()) : null;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        if (getStartTime() != null) {
            LocalDateTime endTime = LocalDateTime.parse(getStartTime(), getFormatter()).plusMinutes(getDuration().toMinutes());
            return endTime.format(getFormatter());
        } else
            return null;
    }

    public DateTimeFormatter getFormatter() {
        return formatter;
    }

    //Переопределения методов, для сравнения по айди
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        AbstractTask otherAbstractTask = (AbstractTask) obj;
        return id == otherAbstractTask.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

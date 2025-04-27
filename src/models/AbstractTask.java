package models;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public abstract class AbstractTask {
    private static int idCounter = 1;
    private int id;
    private String name;
    private String description;
    protected Status status;
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


    abstract public String getStartTime();

    abstract public Duration getDuration();

    abstract public String getEndTime();

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

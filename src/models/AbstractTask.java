package models;

import java.util.Objects;

/*
Принял решение создать абстрактный класс, чтоб решить проблему разной реализации метода setStatus.
НУ и как по моему мнению, повысить гибкость классов Epic и Task.
 */

public abstract class AbstractTask {
    private static int idCounter = 1;
    private int id;
    private String name;
    private String description;
    protected Status status;

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

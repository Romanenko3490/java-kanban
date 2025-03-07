package models;

public class Task extends AbstractTask {

    public Task(String name, String description) {
        super(name, description);
    }

    public Task(String name, String description, Status status) {
        super(name, description);
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{ ID='" + getId() + "' name='" + getName() + "', description='" + getDescription() + "' " +
                "status='" + getStatus() + "'";
    }

}


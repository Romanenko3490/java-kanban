package models;

public class Task extends AbstractTask {

    public Task(String name, String description) {
        super(name, description);
    }

    public Task(String name, String description, Status status) {
        super(name, description);
        this.status = status;
    }

    public Task(Integer id, String name, String description, Status status) {
        super(name, description);
        this.status = status;
        this.setId(id);
    }


    public String stringForSerialize() {
        return getId() + "," + Types.TASK + "," + getName() + "," +
                getStatus() + "," + getDescription();
    }

    @Override
    public String toString() {
        return "Task{ ID='" + getId() + "' name='" + getName() + "', description='" + getDescription() + "' " +
                "status='" + getStatus() + "'";
    }

}


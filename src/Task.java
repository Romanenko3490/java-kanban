public class Task extends AbstractTask {
    private Status status;


    public Task(String name, String description) {
        super(name, description);
        status = Status.NEW;
        TaskManager.addTaskToTaskStore(this);
    }

    public Task(String name, String description, String status) {
        super(name, description);
        setStatus(status);
        TaskManager.addTaskToTaskStore(this);
    }

    void setStatus(String newStatus) {
        if (newStatus.equalsIgnoreCase("new")) {
            this.status = Status.NEW;
        } else if (newStatus.equalsIgnoreCase("in progress") ||
                newStatus.equalsIgnoreCase("in_progress")) {
            this.status = Status.IN_PROGRESS;
        } else if (newStatus.equalsIgnoreCase("done")) {
            this.status = Status.DONE;
        }
    }

    void setStatus(Status newStatus) {
        this.status = newStatus;
    }

    Status getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Task{ ID='" + getId() + "' name='" + getName() + "', description='" + getDescription() + "' " +
                "status='" + getStatus() + "'";
    }

}


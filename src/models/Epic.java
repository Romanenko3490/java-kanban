package models;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class Epic extends AbstractTask {
    private final List<Subtask> subtaskList = new ArrayList<>();
    Duration duration;
    LocalDateTime startTime;

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(Integer id, String name, String description, Status status, String startTime, int durationInMin) {
        super(name, description);
        this.status = status;
        setId(id);
        this.duration = Duration.ofMinutes(durationInMin);
        this.startTime = startTime != null ? LocalDateTime.parse(startTime, getFormatter()) : null;

    }

    private void setStatus() {
        int newCount = 0;
        int completedCount = 0;
        for (Subtask subtask : subtaskList) {
            if (subtask.getStatus().equals(Status.NEW))
                newCount++;
            if (subtask.getStatus().equals(Status.DONE)) {
                completedCount++;
            }
        }
        if (subtaskList.isEmpty() || newCount == subtaskList.size()) {
            this.status = Status.NEW;
        } else if (completedCount == subtaskList.size()) {
            this.status = Status.DONE;
        } else
            this.status = Status.IN_PROGRESS;
    }


    public Status getStatus() {
        setStatus();
        return status;
    }

    private Optional<LocalDateTime> calcStartTime() {

        if (subtaskList.isEmpty()) {
            startTime = null;
            return Optional.empty();
        } else {
            Optional<LocalDateTime> erliestSub = subtaskList.stream()
                    .map(Subtask::getStartTime)
                    .filter(Objects::nonNull)
                    .map(time -> LocalDateTime.parse(time, getFormatter()))
                    .min(LocalDateTime::compareTo);

            erliestSub.ifPresent(time -> startTime = time);
            return erliestSub;
        }

    }

    @Override
    public String getStartTime() {
        calcStartTime();
        return startTime != null ? startTime.format(getFormatter()) : null;
    }

    private Duration calcDuration() {
        duration = Duration.ofMinutes(0);
        if (subtaskList.isEmpty()) {
            return duration;
        } else {
            for (Subtask subtask : subtaskList) {
                if (subtask.getDuration() != null) {
                    duration = duration.plus(subtask.getDuration()); // про иммутабельность класса в теории не написано =(
                }
            }
            return duration;
        }
    }

    @Override
    public Duration getDuration() {
        calcDuration();
        return duration;
    }

    @Override
    public String getEndTime() {
        calcStartTime();
        calcDuration();
        if (calcStartTime().isPresent()) {
            LocalDateTime endTime = LocalDateTime.parse(calcStartTime().get().format(getFormatter()), getFormatter()).plusMinutes(duration.toMinutes());
            return endTime.format(getFormatter());
        } else
            return null;
    }

    public void addSubtask(Subtask subtask) {
        subtaskList.add(subtask);
        subtask.setEpicID(getId());
    }

    public List<Subtask> getSubtaskList() {
        return subtaskList;
    }

    public void createSubtask(String name, String description, String startTime, int durationInMin) {
        Subtask subtask = new Subtask(name, description, startTime, durationInMin);
        subtaskList.add(subtask);
        subtask.setEpicID(getId());
    }

    public void createSubtask(String name, String description, Status status, String startTime, int durationInMin) {
        Subtask subtask = new Subtask(name, description, status, startTime, durationInMin);
        subtaskList.add(subtask);
        subtask.setEpicID(getId());
    }

    public void clearSubtasks() {
        subtaskList.clear();
    }

    public void deleteSubtask(int id) {
        subtaskList.removeIf(subtask -> subtask.getId() == id);
    }


    public String stringForSerialize() {
        return getId() + "," + Types.EPIC + "," + getName() + "," +
                getStatus() + "," + getDescription() + "," + getStartTime() + "," + getDuration().toMinutes() + ",}";
    }

    @Override
    public String toString() {
        return "Epic{ ID='" + getId() + "', name='" + getName() + "', description='" + getDescription() +
                "', status='" + getStatus() + "' ST= " + getStartTime() + "' Dur='" + getDuration().toMinutes() + "min' ET=" +
                getEndTime() + "', contains subtasks:\n" + getSubtaskList();
    }


}


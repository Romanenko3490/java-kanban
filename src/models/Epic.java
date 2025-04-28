package models;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class Epic extends AbstractTask {
    private final List<Subtask> subtaskList = new ArrayList<>();
    private LocalDateTime endTime;


    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(Integer id, String name, String description, Status status, String startTime, int durationInMin) {
        super(name, description);
        this.status = status;
        setId(id);
        setDuration(durationInMin);
        setStartTime(startTime);

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


    @Override
    public String getStartTime() {
        if (subtaskList.isEmpty()) {
            setStartTime((String) null);
            return null;
        } else {
            Optional<LocalDateTime> erliestSubTime = subtaskList.stream()
                    .map(Subtask::getStartTime)
                    .filter(Objects::nonNull)
                    .map(time -> LocalDateTime.parse(time, getFormatter()))
                    .min(LocalDateTime::compareTo);

            if (erliestSubTime.isPresent()) {
                setStartTime(erliestSubTime.get());
                return erliestSubTime.get().format(getFormatter());
            } else
                return null;
        }
    }

    @Override
    public Duration getDuration() {
        setDuration(Duration.ZERO);
        if (subtaskList.isEmpty()) {
            return Duration.ZERO;
        } else {
            Duration temp = Duration.ZERO;
            for (Subtask subtask : subtaskList) {
                if (subtask.getDuration() != null) {
                    temp = temp.plus(subtask.getDuration());
                }
            }
            setDuration(temp);
            return temp;
        }
    }

    @Override
    public String getEndTime() {
        if (subtaskList.isEmpty()) {
            return null;
        } else {
            Optional<LocalDateTime> latestSubTime = subtaskList.stream()
                    .map(Subtask::getEndTime)
                    .filter(Objects::nonNull)
                    .map(time -> LocalDateTime.parse(time, getFormatter()))
                    .max(LocalDateTime::compareTo);
            if (latestSubTime.isPresent()) {
                endTime = latestSubTime.get();
                return endTime.format(getFormatter());
            }
        }
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

    public void createSubtask(String name, String description) {
        Subtask subtask = new Subtask(name, description);
        subtaskList.add(subtask);
        subtask.setEpicID(getId());
    }

    public void clearSubtasks() {
        subtaskList.clear();
    }

    public void deleteSubtask(int id) {
        Subtask subtaskToDelete = null;
        for (Subtask subtask : subtaskList) {
            if (subtask.getId() == id) {
                subtaskToDelete = subtask;
            }
        }
        subtaskList.remove(subtaskToDelete);
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


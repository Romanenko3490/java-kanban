package api.dto;

import models.Status;

import java.time.Duration;

public class SubtaskResponseDto extends TaskResponseDto {
    private final int epicID;

    public SubtaskResponseDto(int id, String name, String description, String status, String startTime, long durationMinutes, String endTime, Integer epicID) {
        super(id, name, description, status, startTime, durationMinutes, endTime);
        this.epicID = epicID;
    }

    public SubtaskResponseDto(int id, String name, String description, Status status, String startTime, long durationMinutes, String endTime, Integer epicID) {
        super(id, name, description, status, startTime, durationMinutes, endTime);
        this.epicID = epicID;
    }

    public SubtaskResponseDto(int id, String name, String description, String status, String startTime, Duration durationMinutes, String endTime, Integer epicID) {
        super(id, name, description, status, startTime, durationMinutes, endTime);
        this.epicID = epicID;
    }

    public SubtaskResponseDto(int id, String name, String description, Status status, String startTime, int durationMinutes, String endTime, Integer epicID) {
        super(id, name, description, status, startTime, durationMinutes, endTime);
        this.epicID = epicID;
    }

    public int getEpicId() {
        return epicID;
    }
}

package api.dto;

import java.util.List;

public class EpicResponseDto extends TaskResponseDto {
    private List<SubtaskResponseDto> subtasks;

    public EpicResponseDto(int id, String name, String description, String status, String startTime, long durationMinutes, String endTime) {
        super(id, name, description, status, startTime, durationMinutes, endTime);
    }

    // Геттер и сеттер для подзадач
    public List<SubtaskResponseDto> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(List<SubtaskResponseDto> subtasks) {
        this.subtasks = subtasks;
    }
}
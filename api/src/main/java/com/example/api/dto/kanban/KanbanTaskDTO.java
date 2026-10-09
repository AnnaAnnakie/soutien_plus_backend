package com.example.api.dto.kanban;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KanbanTaskDTO {
    private Long id;
    private String name;
    private String status;
    private Long kanbanId;
}

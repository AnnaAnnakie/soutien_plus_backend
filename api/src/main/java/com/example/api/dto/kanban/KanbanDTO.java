package com.example.api.dto.kanban;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KanbanDTO {
    private Long id;
    private String name;
    private  String type;
    private Integer room_id;
}

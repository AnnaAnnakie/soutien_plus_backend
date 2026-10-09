package com.example.api.entity.Kanban;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Table(name = "kanban")
@Entity

public class KanbanEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long k_id;

    private String k_name;
    private  String k_type;
    private Integer room_id;

    @OneToMany(mappedBy = "kanban", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<KanbanTask> columns = new ArrayList<>();
}

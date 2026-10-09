package com.example.api.repository.kanban;

import com.example.api.entity.Kanban.KanbanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KanbanRepository extends JpaRepository<KanbanEntity, Long> {
}


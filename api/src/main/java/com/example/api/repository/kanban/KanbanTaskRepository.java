package com.example.api.repository.kanban;

import com.example.api.entity.Kanban.KanbanTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KanbanTaskRepository  extends JpaRepository<KanbanTask, Long> {
}

package com.example.api.repository;

import com.example.api.entity.DocumentEntities.DocumentVersionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentVersionsRepository extends JpaRepository<DocumentVersionEntity, Long> {
}

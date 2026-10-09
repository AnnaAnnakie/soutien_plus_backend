package com.example.api.repository;

import com.example.api.entity.DocumentEntities.AssignementEntity;
import com.example.api.entity.GroupeEntities.Groupe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssignementRepository extends JpaRepository<AssignementEntity, Long> {
}

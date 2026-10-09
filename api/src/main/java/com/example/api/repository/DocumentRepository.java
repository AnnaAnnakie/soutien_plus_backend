package com.example.api.repository;

import com.example.api.entity.DocumentEntities.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {

    @Query("SELECT d FROM DocumentEntity d WHERE d.doc_version = (SELECT MAX(d2.doc_version) FROM DocumentEntity d2 WHERE d2.doc_original_id = d.doc_original_id)")
    List<DocumentEntity> findLatestVersions();

    @Query("SELECT d FROM DocumentEntity d WHERE d.doc_original_id = :originalId ORDER BY d.doc_version ASC")
    List<DocumentEntity> findAllVersionsByOriginalId(@Param("originalId") Long originalId);

    @Query("SELECT d FROM DocumentEntity d WHERE d.doc_original_id = :originalId ORDER BY d.doc_version ASC")
    List<DocumentEntity> findOriginalVersion(@Param("originalId") Long originalId);




}

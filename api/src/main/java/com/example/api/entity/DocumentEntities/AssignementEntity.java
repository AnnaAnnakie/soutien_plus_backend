package com.example.api.entity.DocumentEntities;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

import com.example.api.entity.UtilisateurEntities.Utilisateur;

@Entity
@Table(name = "document_assignments")
@Getter
@Setter
@NoArgsConstructor
public class AssignementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "document_assignments_id_seq")
    @SequenceGenerator(name = "document_assignments_id_seq", sequenceName = "document_assignments_id_seq", allocationSize = 1)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "document_id", nullable = false, foreignKey = @ForeignKey(name = "document_assignments_document_id_fkey"))
    private DocumentEntity document;

    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false, foreignKey = @ForeignKey(name = "document_assignments_recipient_id_fkey"))
    private Utilisateur recipient;

    @Column(nullable = false, length = 255)
    private String status;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(length = 255)
    private String action;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}


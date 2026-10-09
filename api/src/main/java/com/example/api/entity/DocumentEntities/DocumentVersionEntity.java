package com.example.api.entity.DocumentEntities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Table(name = "documentversions")
@Entity
public class DocumentVersionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int docv_id;
    @Column(name = "doc_id", columnDefinition = "bigint")
    private long doc_id;
    private int docv_number;
    private String file_path; // Pas utile
    private long updated_by;
    private String updated_at; //Deja dans doc
}

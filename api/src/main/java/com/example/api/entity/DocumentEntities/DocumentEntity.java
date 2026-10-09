package com.example.api.entity.DocumentEntities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "documents")
@Entity
public class DocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long doc_id;

    @Column(name = "doc_content", columnDefinition = "BYTEA")
    private byte[] doc_content;
    private String doc_name;
    private long doc_original_id;
    private int doc_version;
    private long doc_author;
    private String doc_date_creation;
    private String doc_date_modif;
    private String doc_description;
    private int room_id;
    private String doc_type;




    @Override
    public String toString() {
        return "DocumentEntity{" +
                "id=" + doc_id +
                ", name='" + doc_name + '\'' +
                ", author='" + doc_author + '\'' +
                ", date_creation='" + doc_date_creation + '\'' +
                ", date_modif='" + doc_date_modif + '\'' +
                ", doc type='" + doc_type + '\'' +
                '}';
    }

}

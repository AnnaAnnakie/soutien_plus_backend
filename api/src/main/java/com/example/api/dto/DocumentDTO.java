package com.example.api.dto;

import com.example.api.entity.DocumentEntities.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentDTO {
    private Long id;
    private String name;
    private Long original_id;
    private int version;
    private long author;
    private String date_creation;
    private String date_modif;
    private String type;
    private byte[] content;
    private int room_id;
    private String description;
}

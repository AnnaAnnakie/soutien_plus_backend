package com.example.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentVersionDTO {
    private Long id;
    private Long idRef;
    private long author;
    private String date_creation;
    private String date_modif;
    private String type;
    private byte[] content;
    //private long assignTo;
    private String description;

}

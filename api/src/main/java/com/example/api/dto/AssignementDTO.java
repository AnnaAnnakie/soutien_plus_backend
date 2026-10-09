package com.example.api.dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignementDTO {
    private Integer id;
    private Integer documentId;
    private Integer recipientId;
    private String status;
    private String message;
    private String action;
}
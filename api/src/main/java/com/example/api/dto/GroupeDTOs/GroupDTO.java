package com.example.api.dto.GroupeDTOs;

import com.example.api.dto.RoleDTOs.SimpleRoleDTO;
import com.example.api.dto.UtilisateurDTOs.SimpleUtilisateurDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupDTO {
    private Long roomId;
    private String roomName;
    private String roomSecurityNumber;
    private String roomPersonName;
    private String roomPersonSecondName;
    private String roomDescription;
    private Integer roomInvitationCode;
    private List<SimpleUtilisateurDTO> users;
    private List<SimpleRoleDTO> roles;
}

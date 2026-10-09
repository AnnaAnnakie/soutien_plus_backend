package com.example.api.service;

import com.example.api.dto.DocumentDTO;
import com.example.api.dto.GroupeDTOs.SimpleGroupDTO;
import com.example.api.dto.UtilisateurDTOs.SimpleUtilisateurDTO;
import com.example.api.dto.UtilisateurDTOs.UtilisateurDTO;
import com.example.api.entity.RoleEntities.Role;
import com.example.api.entity.UtilisateurEntities.UsersGroupesRoles;
import com.example.api.entity.UtilisateurEntities.Utilisateur;
import com.example.api.repository.GroupeRepository;
import com.example.api.repository.RoleRepository;
import com.example.api.repository.UsersGroupesRolesRepository;
import com.example.api.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final UsersGroupesRolesRepository usersGroupesRolesRepository;


    public UtilisateurService(UtilisateurRepository utilisateurRepository,
                              UsersGroupesRolesRepository usersGroupesRolesRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.usersGroupesRolesRepository = usersGroupesRolesRepository;
    }

    public UtilisateurDTO getUtilisateurByEmail(String email) {
        Utilisateur utilisateur = utilisateurRepository.findByUsrEmail(email);
        if (utilisateur == null){
            return null;
        } else {
            return getUtilisateurDTO(utilisateur);
        }
    }

    public UtilisateurDTO getUtilisateurById(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findByUsrId(id);
        if (utilisateur == null){
            return null;
        } else {
            return getUtilisateurDTO(utilisateur);
        }
    }


    public UtilisateurDTO getUtilisateurDTO(Utilisateur user) {
        return UtilisateurDTO
                .builder()
                .id(user.getUsrId())
                .name(user.getUsrName())
                .second_name(user.getUsrSecondName())
                .email(user.getUsrEmail())
                .groupes(
                        user.getGroups() != null ?
                            user.getGroups().stream()
                                    .map(group -> new SimpleGroupDTO(group.getRoomId(),group.getRoomName(), group.getRoomDescription(), group.getUsers().size())) // Filtrer ici
                                    .collect(Collectors.toList())
                            : null
                )
                .build();
    }

    public SimpleUtilisateurDTO getSimpleUtilisateurDTO(Utilisateur user) {
        return SimpleUtilisateurDTO
                .builder()
                .id(user.getUsrId())
                .name(user.getUsrName())
                .second_name(user.getUsrSecondName())
                .email(user.getUsrEmail())
                .build();
    }

    public List<SimpleUtilisateurDTO> getUsersFromRoleAndGroupe(Long roleId, Long groupeId) {
        List<UsersGroupesRoles> usersGroupesRoles = usersGroupesRolesRepository.findByGroupIdAndRoleId(groupeId, roleId);
        List<SimpleUtilisateurDTO> utilisateurDTOS = new ArrayList<>();
        for (UsersGroupesRoles user : usersGroupesRoles) {
            Utilisateur utilisateur = utilisateurRepository.findByUsrId(user.getId_user());
            if (utilisateur != null) {
                utilisateurDTOS.add(getSimpleUtilisateurDTO(utilisateur));
            }
        }
        return utilisateurDTOS;
    }

}

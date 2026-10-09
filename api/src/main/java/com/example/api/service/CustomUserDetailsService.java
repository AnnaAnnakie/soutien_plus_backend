package com.example.api.service;


import com.example.api.dto.GroupeDTOs.SimpleGroupDTO;
import com.example.api.dto.UtilisateurDTOs.UtilisateurDTO;
import com.example.api.entity.UtilisateurEntities.Utilisateur;
import com.example.api.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Utilisateur user = utilisateurRepository.findByUsrEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException(email);
        }


        return new org.springframework.security.core.userdetails.User(user.getUsrEmail(), user.getUsrPassword(),
                Collections.emptyList());
    }

    public UtilisateurDTO getUserInfo(String email) {
        Utilisateur user = utilisateurRepository.findByUsrEmail(email);
        return getUtilisateurDTO(user);
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


}

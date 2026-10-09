package com.example.api.repository;

import com.example.api.entity.UtilisateurEntities.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Utilisateur findByUsrEmail(String email);
    Utilisateur findByUsrId(Long usrId);
}

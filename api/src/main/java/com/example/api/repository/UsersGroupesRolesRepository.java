package com.example.api.repository;

import com.example.api.entity.GroupeEntities.GroupesRoles;
import com.example.api.entity.UtilisateurEntities.UsersGroupesRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UsersGroupesRolesRepository extends JpaRepository<UsersGroupesRoles, Long> {
    @Query(value = "SELECT gr FROM UsersGroupesRoles gr WHERE gr.id_groupe = :idGroupe AND gr.id_user = :idUser")
    UsersGroupesRoles findByGroupIdAndUserId(@Param("idGroupe") Long idGroupe, @Param("idUser") Long idUser);


    @Query(value = "SELECT gr FROM UsersGroupesRoles gr WHERE gr.id_groupe = :idGroupe AND gr.id_role = :idRole")
    List<UsersGroupesRoles> findByGroupIdAndRoleId(@Param("idGroupe") Long idGroupe, @Param("idRole") Long idRole);


    @Query(value = "SELECT gr FROM UsersGroupesRoles gr WHERE gr.id_groupe = :idGroupe AND gr.id_user = :idUser AND gr.id_role = :idRole")
    UsersGroupesRoles findByGroupeUserAndRole(@Param("idGroupe") Long idGroupe, @Param("idUser") Long idUser, @Param("idRole") Long idRole);

    @Modifying
    @Transactional
    @Query("UPDATE UsersGroupesRoles gr SET gr.id_role = :newRoleId WHERE gr.id_groupe = :idGroupe AND gr.id_user = :idUser")
    int updateRoleByGroupIdAndUserId(@Param("newRoleId") Long newRoleId, @Param("idGroupe") Long idGroupe, @Param("idUser") Long idUser);
}

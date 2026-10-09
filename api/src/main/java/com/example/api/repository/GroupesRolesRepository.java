package com.example.api.repository;

import com.example.api.entity.GroupeEntities.GroupesRoles;
import com.example.api.entity.RoleEntities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GroupesRolesRepository extends JpaRepository<GroupesRoles, Long> {
    @Query(value = "SELECT gr FROM GroupesRoles gr WHERE gr.id_groupe = :idGroupe AND gr.id_role = :idRole")
    GroupesRoles findByGroupIdAndRoleId(@Param("idGroupe") Long idGroupe, @Param("idRole") Long idRole);
}

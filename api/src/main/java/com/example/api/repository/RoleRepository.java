package com.example.api.repository;

import com.example.api.entity.RoleEntities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long> {


    @Query(value = "SELECT r.id AS id, r.name, r.base, r.description, " +
            "ugr.id_user, ugr.id_groupe " +
            "FROM role r " +
            "RIGHT JOIN users_groupes_roles ugr ON r.id = ugr.id_role " +
            "WHERE ugr.id_groupe = :groupId AND ugr.id_user = :userId",
            nativeQuery = true)
    Role findRolesByGroupIdAndUserId(@Param("groupId") Long groupId,
                                     @Param("userId") Long userId);

    @Query("SELECT r FROM Role r WHERE r.base = true")
    List<Role> findRolesBase();


}

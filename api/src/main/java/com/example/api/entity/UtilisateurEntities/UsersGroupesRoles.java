package com.example.api.entity.UtilisateurEntities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "users_groupes_roles")
public class UsersGroupesRoles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "id_user")
    private Long id_user;

    @Column(name = "id_groupe")
    private Long id_groupe;

    @Column(name = "id_role")
    private Long id_role;

}

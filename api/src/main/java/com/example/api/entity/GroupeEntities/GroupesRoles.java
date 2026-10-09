package com.example.api.entity.GroupeEntities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "groupes_roles")
public class GroupesRoles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id") // Assurez-vous que cette correspondance est correcte dans la base de données
    private Long id;

    @Column(name = "id_groupe")
    private Long id_groupe;

    @Column(name = "id_role")
    private Long id_role;

}

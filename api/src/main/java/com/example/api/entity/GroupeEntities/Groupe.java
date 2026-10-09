package com.example.api.entity.GroupeEntities;
import com.example.api.entity.RoleEntities.Role;
import com.example.api.entity.UtilisateurEntities.Utilisateur;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "room")
public class Groupe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id") // Assurez-vous que cette correspondance est correcte dans la base de données
    private Long roomId;

    @Column(name = "room_name")
    private String roomName;

    @Column(name = "room_security_number")
    private String roomSecurityNumber;

    @Column(name = "room_person_name")
    private String roomPersonName;

    @Column(name = "room_person_second_name")
    private String roomPersonSecondName;

    @Column(name = "room_description")
    private String roomDescription;

    @Column(name = "room_invitation_code")
    private Integer roomInvitationCode;

    @ManyToMany(mappedBy = "groups", fetch = FetchType.LAZY)
    private List<Utilisateur> users;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "groupes_roles",
            joinColumns = @JoinColumn(name = "id_groupe"),
            inverseJoinColumns = @JoinColumn(name = "id_role")
    )
    private List<Role> roles;



}

package com.example.api.entity.UtilisateurEntities;

import com.example.api.entity.GroupeEntities.Groupe;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "user_info")
@Data
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usr_id")
    private Long usrId;

    @Column(name = "usr_password")
    private String usrPassword;

    @Column(name = "usr_name")
    private String usrName;

    @Column(name = "usr_second_name")
    private String usrSecondName;

    @Column(name = "usr_email")
    private String usrEmail;

    @Column(name = "usr_state")
    private String usrState;

    @Column(name = "last_connection")
    private String lastConnection;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_rooms",
            joinColumns = @JoinColumn(name = "usr_id"),
            inverseJoinColumns = @JoinColumn(name = "room_id")
    )
    private List<Groupe> groups;


}

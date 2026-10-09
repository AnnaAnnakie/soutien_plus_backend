package com.example.api.entity.GroupeEntities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "user_rooms")
public class UserGroupe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "usr_id")
    private Long usrId;

    @Column(name = "room_id")
    private Long roomId;
}

package com.example.api.entity.RoleEntities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "permissions")
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "perm_id")
    private long id;

    @Column(name = "permission")
    private String permission;

    @Column(name = "description")
    private String description;

    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    private List<Role> roles;



}

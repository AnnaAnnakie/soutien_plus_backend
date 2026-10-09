package com.example.api.repository;

import com.example.api.entity.GroupeEntities.UserGroupe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserGroupeRepository extends JpaRepository<UserGroupe, Long> {
    UserGroupe findByUsrIdAndRoomId(Long userId, Long roomId);
}

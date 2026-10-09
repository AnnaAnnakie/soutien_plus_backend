package com.example.api.repository;

import com.example.api.entity.GroupeEntities.Groupe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupeRepository extends JpaRepository<Groupe, Long> {
    Groupe findByRoomId(Long roomId);
    Groupe findByRoomInvitationCode(Integer groupeCode);
}


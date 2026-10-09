package com.example.api.repository;

import com.example.api.entity.RoleEntities.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PermissionRepository extends JpaRepository<Permission, Long> {}

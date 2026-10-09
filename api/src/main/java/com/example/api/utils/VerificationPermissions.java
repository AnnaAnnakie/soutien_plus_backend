package com.example.api.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class VerificationPermissions {
    public static boolean isAuthorizedWithPermission(List<String> permissionsOfUsers, List<String> necessariesPermissions) {
        boolean isAuthorized = new HashSet<>(permissionsOfUsers).containsAll(necessariesPermissions);
        return isAuthorized | permissionsOfUsers.contains("*");
    }

    public static ResponseEntity<?> getErrorResponse(String message){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", message));
    }
}

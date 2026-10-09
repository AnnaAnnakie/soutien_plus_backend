package com.example.api.controllers;

import com.example.api.dto.EmailRequestDTO;
import com.example.api.dto.GroupeDTOs.GroupDTO;
import com.example.api.dto.UtilisateurDTOs.UtilisateurDTO;
import com.example.api.service.GroupService;
import com.example.api.service.SendEmailService;
import com.example.api.service.UtilisateurService;
import jakarta.mail.MessagingException;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/soutien/invite")
public class EmailController {

    private SendEmailService sendEmailService;
    private GroupService groupService;
    private UtilisateurService utilisateurService;

    public EmailController(SendEmailService sendEmailService, GroupService groupService, UtilisateurService utilisateurService) {
        this.sendEmailService = sendEmailService;
        this.groupService = groupService;
        this.utilisateurService = utilisateurService;
    }

    @PostMapping("/sendEmail")
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequestDTO emailRequestDTO) throws MessagingException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {

            String userEmail = authentication.getName();

            GroupDTO groupe = groupService.getGroupById(emailRequestDTO.getGroupId());
            UtilisateurDTO user = utilisateurService.getUtilisateurByEmail(userEmail);
            String verificationLink = "http://localhost:4200/invitation?groupCode=" + groupe.getRoomInvitationCode();
            String body = "<p>Bonjour,</p>"
                    + "<p>" + user.getName() + " " + user.getSecond_name() + " vous invite au groupe : " + groupe.getRoomName() + ":</p>"
                    + "<p><a href=\"" + verificationLink + "\" target=\"_blank\">Code d'invitation</a></p>"
                    + "<p>Si vous avez pas une compte Soutien Plus, pensez bien a en crée une! </p>"
                    + "<p>Merci,</p>"
                    + "<p>L'équipe Soutien Plus</p>";
            String subject = "Code d'invitation";

            sendEmailService.sendEmail(emailRequestDTO,body,subject);
            return ResponseEntity.ok().body("Email sent!");
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}

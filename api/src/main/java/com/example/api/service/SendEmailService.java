package com.example.api.service;

import com.example.api.dto.EmailRequestDTO;
import com.example.api.dto.GroupeDTOs.GroupDTO;
import com.example.api.dto.UtilisateurDTOs.UtilisateurDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class SendEmailService {

    private JavaMailSender javaMailSender;
    private UtilisateurService utilisateurService;
    private GroupService groupService;

    @Value("${spring.mail.username}")
    private String fromEmailId;


    public SendEmailService(JavaMailSender javaMailSender, UtilisateurService utilisateurService, GroupService groupService) {
        this.javaMailSender = javaMailSender;
        this.utilisateurService = utilisateurService;
        this.groupService = groupService;
    }
    /**
     * Methode generique pour envoyer des mail.
     * toEmailId mail de la personne a laquelle on envoie le mail
     * body corps du mail envoye
     * On modifie pas le subjet.
     */
    public void sendEmail(EmailRequestDTO emailRequestDTO, String body, String subject) throws MessagingException {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmailId);
            helper.setTo(emailRequestDTO.getToEmail());
            helper.setSubject(subject);
            helper.setText(body, true); // true = email en HTML
            javaMailSender.send(message);
            System.out.println("Email envoyé avec succès à : " + emailRequestDTO.getToEmail());

        } catch (MessagingException e) {
            System.err.println("Erreur lors de l'envoi de l'email : " + e.getMessage());
        }
    }
}

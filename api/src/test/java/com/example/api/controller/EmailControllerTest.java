package com.example.api.controller;

import com.example.api.controllers.EmailController;
import com.example.api.dto.EmailRequestDTO;
import com.example.api.service.SendEmailService;
import com.example.api.service.GroupService;
import com.example.api.service.UtilisateurService;
import com.example.api.dto.GroupeDTOs.GroupDTO;
import com.example.api.dto.UtilisateurDTOs.UtilisateurDTO;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailControllerTest {

    @Mock
    private SendEmailService sendEmailService;

    @Mock
    private GroupService groupService;

    @Mock
    private UtilisateurService utilisateurService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private EmailController emailController;

    @BeforeEach
    void setup() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
    }

    @Test
    public void testSendEmail_Success() throws MessagingException {
        EmailRequestDTO emailRequestDTO = new EmailRequestDTO(1L, "jean.peuplu@gmail.com");

        lenient().when(authentication.isAuthenticated()).thenReturn(true);
        lenient().when(authentication.getName()).thenReturn("jean.peuplu@gmail.com");

        GroupDTO mockGroup = new GroupDTO();
        mockGroup.setRoomInvitationCode(123);
        when(groupService.getGroupById(emailRequestDTO.getGroupId())).thenReturn(mockGroup);

        UtilisateurDTO mockUser = new UtilisateurDTO();
        mockUser.setName("Jean");
        mockUser.setSecond_name("Peuplu");
        when(utilisateurService.getUtilisateurByEmail("jean.peuplu@gmail.com")).thenReturn(mockUser);

        ResponseEntity<String> response = emailController.sendEmail(emailRequestDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Email sent!", response.getBody());
        verify(sendEmailService, times(1)).sendEmail(any(), any(), any());
    }

    @Test
    public void testSendEmail_Unauthorized() throws MessagingException {
        EmailRequestDTO emailRequestDTO = new EmailRequestDTO(1L, "jean.peuplu@gmail.com");

        lenient().when(authentication.isAuthenticated()).thenReturn(false);

        ResponseEntity<String> response = emailController.sendEmail(emailRequestDTO);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

        verify(sendEmailService, never()).sendEmail(any(), any(), any());

        verify(groupService, never()).getGroupById(anyLong());
        verify(utilisateurService, never()).getUtilisateurByEmail(anyString());
    }


}

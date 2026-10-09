package com.example.api.controller;

import com.example.api.controllers.DocumentController;
import com.example.api.dto.AssignementDTO;
import com.example.api.dto.DocumentDTO;
import com.example.api.service.DocumentService;
import com.example.api.service.EncryptionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DocumentControllerTest {

    @Mock
    private DocumentService documentService;

    @Mock
    private EncryptionService encryptionService;

    @InjectMocks
    private DocumentController documentController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // TEST POUR L'UPLOAD D'UN DOCUMENT

    /** Test de uploadDocument
     * On simule l'upload d'un document avec un fichier valide et on verrifie qu'il a bien été envoyé
     */
    @Test
    public void testUploadDocument_Success() throws Exception {
        DocumentDTO documentDTO = new DocumentDTO();
        String metadataJson = objectMapper.writeValueAsString(documentDTO);
        MockMultipartFile file = new MockMultipartFile("content", "test.txt", "text/plain", "Hello World".getBytes()); // Création du fichier mock

        AssignementDTO assignmentDTO = new AssignementDTO();
        String assignmentJson = objectMapper.writeValueAsString(assignmentDTO);
        MockMultipartFile assignmentFile = new MockMultipartFile("assignement", "", "application/json", assignmentJson.getBytes());

        when(encryptionService.encrypt(any())).thenReturn(new byte[]{1, 2, 3});
        doAnswer(invocation -> null).when(documentService).saveDocument(any(DocumentDTO.class), any(AssignementDTO.class));

        ResponseEntity<String> response = documentController.uploadDocument(metadataJson, file, assignmentJson);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    /** Test de uploadDocument
     * On teste l'upload d'un document avec un JSON incorrect
     * La réponse doit indiquer une erreur d'envoi
     */
    @Test
    public void testUploadDocument_Failure() throws Exception {
        MockMultipartFile file = new MockMultipartFile("content", "test.txt", "text/plain", "Hello World".getBytes());
        String invalidJson = "{invalid_json}"; // JSON invalide

        String assignmentJson = null;

        ResponseEntity<String> response = documentController.uploadDocument(invalidJson, file, assignmentJson);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().contains("Erreur lors de l'envoi du document"));
    }

    // TEST POUR LA RÉCUPÉRATION DE TOUS LES DOCUMENTS

    /** Test de getAllDocuments
     * On teste la récupération de tous les documents en simulant une liste vide et on verrifie que ça renvoie une liste vide
     */
    @Test
    public void testGetAllDocuments() {
        when(documentService.getAll()).thenReturn(Collections.emptyList());
        assertTrue(documentController.getAllDocuments().isEmpty());
    }

    // TEST POUR LA RECUPÉRATION DE TOUTES LES VERSIONS DE DOCUMENTS

    /** Test de getAllVersionDocuments
     * On teste la récupération de toutes les versions d'un document en simulant une liste vide et on vérifie que ça renvoie une liste vide.
     */
    @Test
    public void testGetAllVersionDocuments_Empty() {
        // Simulation d'une liste vide de versions de documents
        when(documentService.getAllVersionDocuments(1L)).thenReturn(Collections.emptyList());

        // Appel de la méthode dans le contrôleur
        List<DocumentDTO> response = documentController.getAllVersionDocuments(1L);

        // Vérification que la réponse est une liste vide
        assertTrue(response.isEmpty());
    }

    /** Test de getAllVersionDocuments
     * On teste la récupération de toutes les versions d'un document avec une liste de versions simulée et on vérifie la réponse.
     */
    @Test
    public void testGetAllVersionDocuments_Success() {
        // Simulation d'une liste de versions de documents
        DocumentDTO documentDTO1 = new DocumentDTO();
        DocumentDTO documentDTO2 = new DocumentDTO();
        when(documentService.getAllVersionDocuments(1L)).thenReturn(Arrays.asList(documentDTO1, documentDTO2));

        // Appel de la méthode dans le contrôleur
        List<DocumentDTO> response = documentController.getAllVersionDocuments(1L);

        // Vérification que la liste renvoyée contient les deux documents simulés
        assertEquals(2, response.size());
        assertEquals(documentDTO1, response.get(0));
        assertEquals(documentDTO2, response.get(1));
    }

    /** Test de getAllVersionDocuments
     * On teste la récupération de toutes les versions d'un document pour un ID inexistant
     * La réponse doit être une liste vide
     */
    @Test
    public void testGetAllVersionDocuments_NotFound() {
        // Simulation de l'absence de versions de documents pour l'ID donné
        when(documentService.getAllVersionDocuments(999L)).thenReturn(Collections.emptyList());

        // Appel de la méthode dans le contrôleur
        List<DocumentDTO> response = documentController.getAllVersionDocuments(999L);

        // Vérification que la réponse est une liste vide
        assertTrue(response.isEmpty());
    }


    // TEST POUR LA RÉCUPÉRATION D'UN DOCUMENT PAR ID

    /** Test de getDocument
     * On teste la récupération d'un document avec un ID valide. Le document est récupéré et décrypté correctement.
     * La réponse doit être un document avec le contenu décrypté.
     */
    @Test
    public void testGetDocument_Success() {
        DocumentDTO documentDTO = new DocumentDTO();
        documentDTO.setContent(new byte[]{1, 2, 3});
        when(documentService.findById(1L)).thenReturn(Optional.of(documentDTO));
        when(encryptionService.decrypt(any())).thenReturn("Decrypted Content".getBytes());

        ResponseEntity<DocumentDTO> response = documentController.getDocument(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    /** Test de getDocument
     * On teste la récupération d'un document avec un ID inexistant
     * Renvoie un code 404 NOT_FOUND
     */
    @Test
    public void testGetDocument_NotFound() {
        when(documentService.findById(1L)).thenReturn(Optional.empty());
        ResponseEntity<DocumentDTO> response = documentController.getDocument(1L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // TEST POUR LA SUPPRESSION D'UN DOCUMENT

    /** Test de deleteDocument
     * On teste la suppression d'un document existant et on verifie que la suppression réussit
     */
    @Test
    public void testDeleteDocument_Success() {
        when(documentService.deleteDocument(1L)).thenReturn("Document supprimé avec succès.");
        ResponseEntity<String> response = documentController.deleteDocument(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    /** Test de deleteDocument
     * On teste la suppression d'un document mais la suppression échoue
     * La réponse doit indiquer une erreur
     */
    @Test
    public void testDeleteDocument_Failure() {
        when(documentService.deleteDocument(1L)).thenReturn("Erreur lors de la suppression");
        ResponseEntity<String> response = documentController.deleteDocument(1L);
        assertTrue(response.getBody().contains("Erreur"));
    }

    // TEST POUR LE RENOMMAGE D'UN DOCUMENT

    /** Test de renameDocument
     * On teste le renommage d'un document et on verifie si l'opération réussit
     */
    @Test
    public void testRenameDocument_Success() {
        DocumentController.RenameRequest request = new DocumentController.RenameRequest();
        request.setName("New Name");
        when(documentService.updateDocumentName(1L, "New Name")).thenReturn("Nom du document mis à jour avec succès");
        ResponseEntity<String> response = documentController.renameDocument(1L, request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    /** Test de renameDocument
     * On teste le renommage d'un document qui échoue
     * La réponse doit indiquer une erreur
     */
    @Test
    public void testRenameDocument_Failure() {
        DocumentController.RenameRequest request = new DocumentController.RenameRequest();
        request.setName("New Name");
        when(documentService.updateDocumentName(1L, "New Name")).thenReturn("Erreur lors du renommage");
        ResponseEntity<String> response = documentController.renameDocument(1L, request);
        assertTrue(response.getBody().contains("Erreur"));
    }

    /** Test de renameDocument
     * On teste le renommage d'un document avec un nom vide
     * Renvoie un code 400 BAD_REQUEST
     */
    @Test
    public void testRenameDocument_EmptyName() {
        DocumentController.RenameRequest request = new DocumentController.RenameRequest();
        request.setName(""); 
        ResponseEntity<String> response = documentController.renameDocument(1L, request);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}


package com.example.api.controllers;


import com.example.api.dto.AssignementDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.api.service.EncryptionService;
import com.example.api.dto.DocumentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.api.service.DocumentService;
import org.springframework.web.multipart.MultipartFile;
import java.util.Optional;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/soutien")
public class DocumentController {

    private final EncryptionService encryptionService;
    private final DocumentService documentService; // Service pour traiter la chaîne et interagir avec la base de données

    public DocumentController(EncryptionService encryptionService, DocumentService documentService) {
        this.encryptionService = encryptionService;
        this.documentService = documentService;
    }



    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadDocument(
            @RequestPart("metadata") String metadataJson,
            @RequestPart("content") MultipartFile contentFile,
            @RequestPart(value = "assignement", required = false) String assignmentJson) {

        System.out.println("JSON reçu : " + metadataJson);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            //Conversion du JSON en DTO
            DocumentDTO documentDTO = objectMapper.readValue(metadataJson, DocumentDTO.class);
            System.out.println(contentFile);

            //Lire et encrypter le contenu du fichier
            byte[] content = contentFile.getBytes();
            byte[] encryptedContent = encryptionService.encrypt(content);
            documentDTO.setContent(encryptedContent);

            //Si un assignment est envoyé, le parser et l'associer
            AssignementDTO assignmentDTO = null;
            if (assignmentJson != null && !assignmentJson.isEmpty()) {
                assignmentDTO = objectMapper.readValue(assignmentJson, AssignementDTO.class);
                System.out.println("ASSIGNEMENT"+assignmentDTO.toString());
            }

            //Sauvegarde du document avec ou sans assignment
            documentService.saveDocument(documentDTO, assignmentDTO);

            return ResponseEntity.ok("{\"status\":\"success\",\"message\":\"Document reçu avec succès\"}");

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"status\":\"error\",\"message\":\"Erreur lors de l'envoi du document\"}");
        }
    }

    @GetMapping("/documents")
    public List<DocumentDTO> getAllDocuments() {
        // Retourner la liste des documents en DTO
        return documentService.getAll();
    }

    @GetMapping("/documents/{id}")
    public List<DocumentDTO> getAllVersionDocuments(@PathVariable Long id) {
        // Retourner la liste des documents en DTO
        return documentService.getAllVersionDocuments(id);
    }

    @GetMapping("/document/{id}")
    public ResponseEntity<DocumentDTO> getDocument(@PathVariable Long id) {
        Optional<DocumentDTO> optionalDocument = documentService.findById(id);
        if (optionalDocument.isPresent()) {
            DocumentDTO documentDTO = optionalDocument.get();

            // Déchiffrer le contenu
            byte[] decryptedContent = encryptionService.decrypt(documentDTO.getContent());
            documentDTO.setContent(decryptedContent);

            return ResponseEntity.ok(documentDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/document/delete/{id}")
    public ResponseEntity<String> deleteDocument(@PathVariable Long id) {
        String result = documentService.deleteDocument(id);
        if ("Document supprimé avec succès.".equals(result)) {
            System.out.println("suprr cbn");
            return ResponseEntity.ok("{\"status\":\"success\",\"message\":\"Document supprimé avec succès\"}");
        } else {
            return ResponseEntity.ok("{\"status\":\"error\",\"message\":\"Erreur lors de la suppression\"}");
        }
    }

    @PatchMapping("/document/rename/{id}")
    public ResponseEntity<String> renameDocument(@PathVariable Long id, @RequestBody RenameRequest request) {
        if (request.getName() == null || request.getName().isEmpty()) {
            return ResponseEntity.badRequest().body("{\"status\":\"error\",\"message\":\"Le nom ne peut pas être vide\"}");
        }

        String result = documentService.updateDocumentName(id, request.getName());
        if ("Nom du document mis à jour avec succès".equals(result)) {
            return ResponseEntity.ok("{\"status\":\"success\",\"message\":\"Document renommé avec succès\"}");
        } else {
            return ResponseEntity.ok("{\"status\":\"error\",\"message\":\"Erreur lors du renommage\"}");
        }
    }

    // Classe interne pour gérer les requêtes de renommage
    public static class RenameRequest {
        private String name;

        // Getters et Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
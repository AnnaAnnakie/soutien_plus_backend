package com.example.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.api.dto.kanban.KanbanDTO;
import com.example.api.service.KanbanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class KanbanController {

    @Autowired
    private KanbanService kanbanService;

    @PostMapping(value = "kanban/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> addKanban(
            @RequestPart("metadata") String metadataJson ){
        System.out.println("JSON reçu : " + metadataJson);

        // Parse metadata JSON
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            KanbanDTO metadata = objectMapper.readValue(metadataJson, KanbanDTO.class);
            metadata.setId(null);


            // Enregistrer via le service
            kanbanService.saveKanban(metadata);

            // Traitement du fichier
            //String originalFileName = contentFile.getOriginalFilename();
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.ok("{\"status\":\"error\",\"message\":\"Erreur\"}");
        }
        return ResponseEntity.ok("{\"status\":\"success\",\"message\":\"Kanban reçu avec succès\"}");
    }


    @GetMapping("/kanbans")
    public List<KanbanDTO> getAllKanban() {
        return kanbanService.getAll();
    }

    @GetMapping("/kanban/{id}")
    public ResponseEntity<KanbanDTO> getKanban(@PathVariable Long id) {
        Optional<KanbanDTO> optionalKanbanDTO = kanbanService.findById(id);
        if (optionalKanbanDTO.isPresent()) {
            KanbanDTO kanban = optionalKanbanDTO.get();
            return ResponseEntity.ok(kanban);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/kanban/delete/{id}")
    public ResponseEntity<String> deleteKanban(@PathVariable Long id) {
        String result = kanbanService.deleteKanban(id);
        if ("Kanban supprimé avec succès.".equals(result)) {
            return ResponseEntity.ok("{\"status\":\"success\",\"message\":\"Kanban supprimé avec succès\"}");
        } else {
            return ResponseEntity.ok("{\"status\":\"error\",\"message\":\"Erreur\"}");
        }
    }

    @PatchMapping("/kanban/rename/{id}")
    public ResponseEntity<String> renameKanban(@PathVariable Long id, @RequestBody KanbanController.RenameRequest request) {
        if (request.getName() == null || request.getName().isEmpty()) {
            return ResponseEntity.badRequest().body("{\"status\":\"error\",\"message\":\"Le nom ne peut pas être vide\"}");
        }
        String result = kanbanService.updateKanbanName(id, request.getName());
        if ("Nom du kanban mis à jour avec succès".equals(result)) {
            return ResponseEntity.ok("{\"status\":\"success\",\"message\":\"Kanban renommé avec succès\"}");
        } else {
            return ResponseEntity.ok("{\"status\":\"error\",\"message\":\"Erreur\"}");
        }
    }


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

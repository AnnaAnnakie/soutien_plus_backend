package com.example.api.controller;

import com.example.api.controllers.KanbanController;
import com.example.api.dto.kanban.KanbanDTO;
import com.example.api.service.KanbanService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KanbanControllerTest {

    @Mock
    private KanbanService kanbanService;

    @InjectMocks
    private KanbanController kanbanController;

    /** Test de addKanban
     */
    @Test
    void testAddKanban() {
        String kanbanJson = "{\"id\":null,\"name\":\"Kanban Test\",\"type\":\"test\",\"room_id\":123}";
        when(kanbanService.saveKanban(any(KanbanDTO.class))).thenReturn("Kanban enregistré avec succès.");

        ResponseEntity<String> response = kanbanController.addKanban(kanbanJson);

        assertEquals(ResponseEntity.ok("{\"status\":\"success\",\"message\":\"Kanban reçu avec succès\"}"), response);
        verify(kanbanService).saveKanban(any(KanbanDTO.class));
    }

    /** Test de getAllKanban
     * On simule une liste de kaban et on essaye de toutes les récupérer
     */
    @Test
    void testGetAllKanban() {
        List<KanbanDTO> kanbanList = List.of(
                new KanbanDTO(1L, "Kanban Test1", "test", 666),
                new KanbanDTO(2L, "Kanban Test2", "test", 123)
        );
        when(kanbanService.getAll()).thenReturn(kanbanList);

        List<KanbanDTO> response = kanbanController.getAllKanban();

        assertEquals(2, response.size());
        verify(kanbanService).getAll();
    }

    /** Test de getKanbanById
     * On crée un kanban avec un Id quelconque et on essaye de le récupérer
     */
    @Test
    void testGetKanbanById() {
        Long kanbanId = 1L;
        KanbanDTO kanbanDTO = new KanbanDTO(kanbanId, "Kanban Test", "test", 666);
        when(kanbanService.findById(kanbanId)).thenReturn(Optional.of(kanbanDTO));

        ResponseEntity<KanbanDTO> response = kanbanController.getKanban(kanbanId);

        assertEquals(ResponseEntity.ok(kanbanDTO), response);
        verify(kanbanService).findById(kanbanId);
    }

    /** Test de deleteKanban
     * On verifie que le kanban se supprime bien grâce au service
     */
    @Test
    void testDeleteKanban() {
        Long kanbanId = 1L;
        when(kanbanService.deleteKanban(kanbanId)).thenReturn("Kanban supprimé avec succès.");

        ResponseEntity<String> response = kanbanController.deleteKanban(kanbanId);

        assertEquals(ResponseEntity.ok("{\"status\":\"success\",\"message\":\"Kanban supprimé avec succès\"}"), response);
        verify(kanbanService).deleteKanban(kanbanId);
    }

    /** Test de renameKanban
     * On tente d'attribuer un nouveau nom à un kanban et on vérifie si la modification a bien été prise en compte
     */
    @Test
    void testRenameKanban() {
        Long kanbanId = 1L;
        KanbanController.RenameRequest request = new KanbanController.RenameRequest();
        request.setName("Nouveau Nom");
        when(kanbanService.updateKanbanName(kanbanId, request.getName()))
                .thenReturn("Nom du kanban mis à jour avec succès");

        ResponseEntity<String> response = kanbanController.renameKanban(kanbanId, request);

        assertEquals(ResponseEntity.ok("{\"status\":\"success\",\"message\":\"Kanban renommé avec succès\"}"), response);
        verify(kanbanService).updateKanbanName(kanbanId, request.getName());
    }

}

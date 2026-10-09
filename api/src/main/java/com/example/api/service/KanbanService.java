package com.example.api.service;

import com.example.api.entity.Kanban.KanbanEntity;
import com.example.api.entity.Kanban.KanbanTask;
import com.example.api.dto.kanban.KanbanDTO;
import com.example.api.dto.kanban.KanbanTaskDTO;

import com.example.api.repository.kanban.KanbanRepository;
import com.example.api.repository.kanban.KanbanTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class KanbanService{
    @Autowired
    private KanbanRepository kanbanRepository;
    private KanbanTaskRepository kanbanTaskRepository;

    // Convert KanbanEntity to KanbanDTO
    public KanbanDTO toDTO(KanbanEntity entity) {
        KanbanDTO dto = new KanbanDTO();
        dto.setId(entity.getK_id());
        dto.setName(entity.getK_name());
        dto.setType(entity.getK_type());
        return dto;
    }

    // Convert KanbanDTO to KanbanEntity
    public KanbanEntity toEntity(KanbanDTO dto) {
        KanbanEntity entity = new KanbanEntity();
        entity.setK_id(dto.getId());
        entity.setK_name(dto.getName());
        entity.setK_type(dto.getType());
        return entity;
    }

    /**
     * Enregistrer un Kanban
     * @param kanbanDTO Le DTO représentant le Kanban
     * @return Le message de succès
     */
    public String saveKanban(KanbanDTO kanbanDTO) {
        KanbanEntity kanbanEntity = new KanbanEntity();
        kanbanEntity.setK_name(kanbanDTO.getName());
        // Autres mappings si nécessaire
        kanbanRepository.save(kanbanEntity);
        return "Kanban enregistré avec succès.";
    }


    // Récupérer tous les documents sous forme de DTO
    public List<KanbanDTO> getAll() {
        return kanbanRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<KanbanDTO> findById(Long id) {
            return kanbanRepository.findById(id).map(this::toDTO);
        }

    /**
     * Modifier le nom d'un Kanban
     * @param id L'ID du Kanban
     * @param newName Le nouveau nom
     * @return Le message de succès
     */
    public String updateKanbanName(Long id, String newName) {
        KanbanEntity kanbanEntity = kanbanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Kanban introuvable avec l'ID : " + id));
        kanbanEntity.setK_name(newName);
        kanbanRepository.save(kanbanEntity);
        return "Nom du Kanban mis à jour avec succès.";
    }

    /**
     * Supprimer un Kanban
     * @param id L'ID du Kanban
     * @return Le message de succès
     */
    public String deleteKanban(Long id) {
        if (kanbanRepository.existsById(id)) {
            kanbanRepository.deleteById(id);
            return "Kanban supprimé avec succès.";
        } else {
            return "Kanban introuvable.";
        }
    }

    // ---- TASK SERVICE ----

    /**
     * Ajouter une tâche à un Kanban
     * @param kanbanId L'ID du Kanban
     * @param taskDTO Le DTO représentant la tâche
     * @return La tâche créée
     */
    public KanbanTaskDTO addTask(Long kanbanId, KanbanTaskDTO taskDTO) {
        KanbanEntity kanbanEntity = kanbanRepository.findById(kanbanId)
                .orElseThrow(() -> new IllegalArgumentException("Kanban introuvable avec l'ID : " + kanbanId));

        KanbanTask task = new KanbanTask();
        task.setName(taskDTO.getName());
        task.setStatus(taskDTO.getStatus() != null ? taskDTO.getStatus() : "TODO");
        task.setKanban(kanbanEntity);
        KanbanTask savedTask = kanbanTaskRepository.save(task);

        KanbanTaskDTO savedTaskDTO = new KanbanTaskDTO();
        savedTaskDTO.setId(savedTask.getId());
        savedTaskDTO.setName(savedTask.getName());
        savedTaskDTO.setStatus(savedTask.getStatus());
        savedTaskDTO.setKanbanId(savedTask.getKanban().getK_id());
        return savedTaskDTO;
    }

    /**
     * Supprimer une tâche par son ID
     * @param taskId L'ID de la tâche à supprimer
     */
    public void deleteTask(Long taskId) {
        if (!kanbanTaskRepository.existsById(taskId)) {
            throw new IllegalArgumentException("Tâche introuvable avec l'ID : " + taskId);
        }
        kanbanTaskRepository.deleteById(taskId);
    }

    /**
     * Renommer une tâche
     * @param taskId L'ID de la tâche
     * @param newName Le nouveau nom
     * @return La tâche mise à jour
     */
    public KanbanTaskDTO renameTask(Long taskId, String newName) {
        KanbanTask task = kanbanTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Tâche introuvable avec l'ID : " + taskId));

        task.setName(newName);
        KanbanTask updatedTask = kanbanTaskRepository.save(task);

        KanbanTaskDTO updatedTaskDTO = new KanbanTaskDTO();
        updatedTaskDTO.setId(updatedTask.getId());
        updatedTaskDTO.setName(updatedTask.getName());
        updatedTaskDTO.setStatus(updatedTask.getStatus());
        updatedTaskDTO.setKanbanId(updatedTask.getKanban().getK_id());
        return updatedTaskDTO;
    }
}

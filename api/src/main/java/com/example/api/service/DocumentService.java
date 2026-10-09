package com.example.api.service;

import com.example.api.dto.AssignementDTO;
import com.example.api.dto.DocumentDTO;
import com.example.api.entity.DocumentEntities.AssignementEntity;
import com.example.api.repository.AssignementRepository;
import com.example.api.repository.DocumentRepository;
import com.example.api.repository.DocumentVersionsRepository;
import com.example.api.repository.UtilisateurRepository;
import jdk.jshell.execution.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.api.entity.DocumentEntities.DocumentEntity;

import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Optional;


@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private AssignementRepository assignementRepository;
    @Autowired
    private UtilisateurRepository utilisateurRepository;

    // Convert DocumentEntity to DocumentDTO
    public DocumentDTO toDTO(DocumentEntity entity) {
        DocumentDTO dto = new DocumentDTO();
        dto.setId(entity.getDoc_id());
        dto.setVersion(entity.getDoc_version());
        dto.setOriginal_id(entity.getDoc_original_id());
        dto.setName(entity.getDoc_name());
        dto.setAuthor(entity.getDoc_author());
        dto.setDate_creation(entity.getDoc_date_creation());
        dto.setDate_modif(entity.getDoc_date_modif());
        dto.setType(entity.getDoc_type());
        dto.setContent(entity.getDoc_content());
        dto.setRoom_id(entity.getRoom_id());
        dto.setDescription(entity.getDoc_description());
        //dto.setAssignTo(entity.getDoc);
        return dto;
    }

    // POur un nouveau document: Convert DocumentDTO to DocumentEntity
    public DocumentEntity dtoToEntity(DocumentDTO dto) {
        DocumentEntity entity = new DocumentEntity();
        entity.setDoc_name(dto.getName());
        entity.setDoc_original_id(dto.getOriginal_id());
        entity.setDoc_version(dto.getVersion());
        entity.setDoc_author(dto.getAuthor());
        entity.setDoc_date_creation(dto.getDate_creation());
        entity.setDoc_date_modif(dto.getDate_modif());
        entity.setDoc_type(dto.getType());
        entity.setDoc_content(dto.getContent());
        entity.setRoom_id(dto.getRoom_id());
        entity.setDoc_description(dto.getDescription());
        return entity;
    }

    //DTO to Entity pour la création de nouveau document
    public DocumentEntity createDtoToEntity(DocumentDTO dto) {
        DocumentEntity entity = new DocumentEntity();
        entity.setDoc_name(dto.getName());
        entity.setDoc_version(dto.getVersion());
        entity.setDoc_author(dto.getAuthor());
        entity.setDoc_date_creation(dto.getDate_creation());
        entity.setDoc_date_modif(dto.getDate_modif());
        entity.setDoc_type(dto.getType());
        entity.setDoc_content(dto.getContent());
        entity.setRoom_id(dto.getRoom_id());
        entity.setDoc_description(dto.getDescription());
        return entity;
    }

    /** TODO:
    private DocumentVersionEntity newVersionDocument (long idRef){
        DocumentVersionEntity newDocumentVersionEntity = new DocumentVersionEntity();
        return new DocumentVersionEntity(1,idRef,)
    }**/

    // Enregistrer un document via DTO
    public String saveDocument(DocumentDTO documentDTO, AssignementDTO assignmentDTO) {
        DocumentEntity documentEntity = null;

        //Modification d'un document existant
        if (documentDTO.getId() != null) {
            Optional<DocumentEntity> existingDocumentOpt = documentRepository.findById(documentDTO.getId());

            if (existingDocumentOpt.isPresent()) {
                DocumentEntity existingDocument = existingDocumentOpt.get();
                documentDTO.setVersion(existingDocument.getDoc_version() + 1);
                documentDTO.setOriginal_id(existingDocument.getDoc_original_id());
                documentEntity = dtoToEntity(documentDTO);
            }
        }

        //Création d'un nouveau document
        if (documentEntity == null) {
            documentDTO.setVersion(1);
            documentEntity = createDtoToEntity(documentDTO);
            DocumentEntity savedDocument = documentRepository.save(documentEntity);
            documentEntity.setDoc_original_id(savedDocument.getDoc_id());
        }

        //Sauvegarde du document
        documentEntity = documentRepository.save(documentEntity);

        //Gestion de l'Assignment s'il existe
        if (assignmentDTO != null) {
            AssignementEntity assignementEntity = new AssignementEntity();
            assignementEntity.setDocument(documentEntity);
            assignementEntity.setRecipient(utilisateurRepository.findById(assignmentDTO.getRecipientId().longValue())
                    .orElseThrow(() -> new RuntimeException("Utilisateur introuvable")));
            assignementEntity.setStatus(assignmentDTO.getStatus());
            assignementEntity.setMessage(assignmentDTO.getMessage());
            assignementEntity.setAction(assignmentDTO.getAction());

            assignementRepository.save(assignementEntity);
        }

        return "Document enregistré avec succès.";
    }




    // Récupérer tous les documents sous forme de DTO
    public List<DocumentDTO> getAll() {
        return documentRepository.findLatestVersions().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }


    //TODO: Récupérer tous les documents sous forme de DTO
    public List<DocumentDTO> getAllVersionDocuments(long id) {
        return documentRepository.findAllVersionsByOriginalId(id).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }



    // Trouver un document par son ID et retourner un DTO
    public Optional<DocumentDTO> findById(Long id) {
        return documentRepository.findById(id).map(this::toDTO);
    }

    // Méthode pour modifier le nom d'un document
    public String updateDocumentName(Long id, String newName) {
        Optional<DocumentEntity> optionalDocument = documentRepository.findById(id);
        if (optionalDocument.isPresent()) {
            DocumentEntity document = optionalDocument.get();
            document.setDoc_name(newName);
            documentRepository.save(document);
            return "Nom du document mis à jour avec succès";
        } else {
            return "Document introuvable.";
        }
    }

    // Méthode pour supprimer un document par son ID
    public String deleteDocument(Long id) {
        Optional<DocumentEntity> documentOpt = documentRepository.findById(id);

        if (documentOpt.isPresent()) {
            DocumentEntity document = documentOpt.get();

            // Récupérer toutes les versions restantes (sans celle supprimée)
            List<DocumentEntity> remainingVersions = documentRepository.findAllVersionsByOriginalId(document.getDoc_original_id());

            // Supprimer le document
            documentRepository.deleteById(id);

            // SI le document courant était l'original
            if (document.getDoc_original_id() == document.getDoc_id()) {
                //Si il y a d'autres versions que l'original
                if (remainingVersions.size()>1) {
                    // Prendre la plus ancienne version restante et la définir comme nouvelle originale
                    DocumentEntity newOriginal = remainingVersions.get(0);
                    newOriginal.setDoc_original_id(newOriginal.getDoc_id()); // Devient l'original
                    documentRepository.save(newOriginal);

                    // Mettre à jour les autres versions pour qu'elles pointent vers le nouvel original
                    for (DocumentEntity doc : remainingVersions) {
                        doc.setDoc_original_id(newOriginal.getDoc_id());
                        documentRepository.save(doc);
                    }
                }
            }

            // Réindexer les versions restantes pour qu'elles soient consécutives (1, 2, 3, ...)
            List<DocumentEntity> orderedVersions = documentRepository.findAllVersionsByOriginalId(document.getDoc_original_id());
            orderedVersions.sort(Comparator.comparing(DocumentEntity::getDoc_version)); // Trier par version

            int newVersionNumber = 1;
            for (DocumentEntity doc : orderedVersions) {
                doc.setDoc_version(newVersionNumber++);
                documentRepository.save(doc);
            }

            return "Document supprimé avec succès";
        } else {
            return "Document introuvable.";
        }
    }

}

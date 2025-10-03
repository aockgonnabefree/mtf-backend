package ku.cs.mtf_backend.service;

import ku.cs.mtf_backend.dto.request.CreateDocumentPayload;
import ku.cs.mtf_backend.entity.Document;
import ku.cs.mtf_backend.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentService {
    private DocumentRepository documentRepository;

    @Autowired
    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public void createAndSaveDocuments(String employeeId, List<CreateDocumentPayload> documentPayloads) {
        if (documentPayloads == null || documentPayloads.isEmpty()) {
            return;
        }

        List<Document> documentsToSave = new ArrayList<>();
        documentPayloads.forEach(docPayload -> {
            Document doc = Document.builder()
                    .id(UUID.randomUUID().toString())
                    .type(docPayload.getType())
                    .expiryDate(LocalDate.parse(docPayload.getExpiryDate()))
                    .employeeId(employeeId)
                    .build();
            documentsToSave.add(doc);
        });

        documentRepository.saveAll(documentsToSave);
    }
}

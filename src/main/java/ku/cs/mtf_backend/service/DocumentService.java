package ku.cs.mtf_backend.service;

import ku.cs.mtf_backend.dto.request.CreateAddressPayload;
import ku.cs.mtf_backend.dto.request.CreateDocumentPayload;
import ku.cs.mtf_backend.entity.Document;
import ku.cs.mtf_backend.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    @Transactional
    public void syncDocumentsForEmployee(String employeeId, List<CreateDocumentPayload> newDocumentsPayload) {
        // 1. ดึงเอกสารเก่าทั้งหมดของลูกจ้างคนนี้ขึ้นมาจาก DB
        Map<String, Document> oldDocumentsMap = documentRepository.findAllByEmployeeId(employeeId)
                .stream()
                .collect(Collectors.toMap(Document::getType, Function.identity()));

        // 2. เตรียม List สำหรับเก็บเอกสารที่จะ (U)pdate, (I)nsert
        List<Document> documentsToUpdate = new ArrayList<>();
        List<Document> documentsToInsert = new ArrayList<>();

        // 3. วนลูปข้อมูลใหม่เพื่อเปรียบเทียบและแยกกอง
        for (CreateDocumentPayload newDocPayload : newDocumentsPayload) {
            Document oldDoc = oldDocumentsMap.get(newDocPayload.getType());

            if (oldDoc != null) {
                // ถ้าเจอเอกสารชนิดเดียวกันในข้อมูลเก่า (เป็นเคส UPDATE)
                // ตรวจสอบว่าวันหมดอายุเปลี่ยนไปหรือไม่
                if (!oldDoc.getExpiryDate().equals(LocalDate.parse(newDocPayload.getExpiryDate()))) {
                    oldDoc.setExpiryDate(LocalDate.parse(newDocPayload.getExpiryDate()));
                    documentsToUpdate.add(oldDoc);
                }
                // ลบออกจาก Map เพื่อให้รู้ว่าตัวไหนถูกประมวลผลไปแล้ว
                oldDocumentsMap.remove(newDocPayload.getType());
            } else {
                // ถ้าไม่เจอ (เป็นเคส INSERT)
                documentsToInsert.add(Document.builder()
                        .id(UUID.randomUUID().toString())
                        .type(newDocPayload.getType())
                        .expiryDate(LocalDate.parse(newDocPayload.getExpiryDate()))
                        .employeeId(employeeId)
                        .build());
            }
        }

        // 4. เอกสารที่ยังเหลืออยู่ใน Map คือเอกสารที่ต้องลบทิ้ง (DELETE)
        if (!oldDocumentsMap.isEmpty()) {
            List<String> idsToDelete = oldDocumentsMap.values().stream()
                    .map(Document::getId)
                    .collect(Collectors.toList());
            documentRepository.deleteByIds(idsToDelete);
        }

        // 5. สั่งให้ Repository ทำงานแบบ Batch
        if (!documentsToUpdate.isEmpty()) {
            documentRepository.batchUpdate(documentsToUpdate);
        }
        if (!documentsToInsert.isEmpty()) {
            documentRepository.batchInsert(documentsToInsert);
        }
    }
}

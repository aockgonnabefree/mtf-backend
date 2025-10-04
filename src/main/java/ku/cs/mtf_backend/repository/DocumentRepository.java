package ku.cs.mtf_backend.repository;

import ku.cs.mtf_backend.entity.Document;

import java.util.List;
import java.util.Map;

public interface DocumentRepository {
    void saveAll(List<Document> documents);
    List<Document> findAllByEmployeeId(String employeeId);
    void batchUpdate(List<Document> documents);
    void batchInsert(List<Document> documents);
    void deleteByIds(List<String> documentIds);

    // Get document statuses for multiple employees
    Map<String, List<Document>> findDocumentsByEmployeeIds(List<String> employeeIds);
}

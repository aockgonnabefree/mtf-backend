package ku.cs.mtf_backend.repository;

import ku.cs.mtf_backend.entity.Document;

import java.util.List;

public interface DocumentRepository {
    void saveAll(List<Document> documents);
}

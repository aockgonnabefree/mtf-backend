package ku.cs.mtf_backend.repository.jdbc;

import ku.cs.mtf_backend.entity.Document;
import ku.cs.mtf_backend.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public class JdbcDocumentRepository implements DocumentRepository {

    private JdbcTemplate jdbcTemplate;
    @Autowired
    public JdbcDocumentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void saveAll(List<Document> documents) {
        if (documents == null || documents.isEmpty()) {
            return;
        }

        String sql = """
            INSERT INTO DOCUMENT (Id, Type, Expiry_date, Employee_id)
            VALUES (?, CAST(? AS document_type), ?, ?)
            """;

        jdbcTemplate.batchUpdate(sql,
                documents,
                100, // Batch size
                (ps, document) -> {
                    ps.setString(1, document.getId());
                    ps.setString(2, document.getType());
                    ps.setDate(3, Date.valueOf(document.getExpiryDate()));
                    ps.setString(4, document.getEmployeeId());
                });
    }
}

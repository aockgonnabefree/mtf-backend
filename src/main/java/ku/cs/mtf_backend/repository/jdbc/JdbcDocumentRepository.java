package ku.cs.mtf_backend.repository.jdbc;

import ku.cs.mtf_backend.entity.Document;
import ku.cs.mtf_backend.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
                }
    );
    }
    @Override
    public List<Document> findAllByEmployeeId(String employeeId) {
        String sql = "SELECT * FROM DOCUMENT WHERE Employee_id = ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Document.class), employeeId);
    }

    @Override
    public void batchInsert(List<Document> documents) {
        if (documents == null || documents.isEmpty()) {
            return;
        }
        String sql = "INSERT INTO DOCUMENT (Id, Type, Expiry_date, Employee_id) VALUES (?, CAST(? AS document_type), ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Document doc = documents.get(i);
                ps.setString(1, doc.getId());
                ps.setString(2, doc.getType());
                ps.setDate(3, Date.valueOf(doc.getExpiryDate()));
                ps.setString(4, doc.getEmployeeId());
            }

            @Override
            public int getBatchSize() {
                return documents.size();
            }
        });
    }

    @Override
    public void batchUpdate(List<Document> documents) {
        if (documents == null || documents.isEmpty()) {
            return;
        }
        String sql = "UPDATE DOCUMENT SET Expiry_date = ? WHERE Id = ?";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Document doc = documents.get(i);
                ps.setDate(1, Date.valueOf(doc.getExpiryDate()));
                ps.setString(2, doc.getId());
            }

            @Override
            public int getBatchSize() {
                return documents.size();
            }
        });
    }

    @Override
    public void deleteByIds(List<String> documentIds) {
        if (documentIds == null || documentIds.isEmpty()) {
            return;
        }
        String sql = "DELETE FROM DOCUMENT WHERE Id IN (?)";

        String inSql = String.join(",", java.util.Collections.nCopies(documentIds.size(), "?"));
        sql = sql.replace("?", inSql);

        jdbcTemplate.update(sql, documentIds.toArray());
    }
}
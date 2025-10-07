package ku.cs.mtf_backend.repository.jdbc;

import ku.cs.mtf_backend.entity.Bill;
import ku.cs.mtf_backend.repository.BillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcBillRepository implements BillRepository {

    private JdbcClient jdbcClient;

    @Autowired
    public JdbcBillRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Bill save(Bill bill) {
        String sql = """
            INSERT INTO BILL (Id, Step_index, Step_name, Price, Status, Created_at, Paid_at, Work_id)
            VALUES (:id, :stepIndex, :stepName, :price, CAST(:status AS bill_status), :createdAt, :paidAt, :workId)
            """;

        jdbcClient.sql(sql)
                .param("id", bill.getId())
                .param("stepIndex", bill.getStepIndex())
                .param("stepName", bill.getStepName())
                .param("price", bill.getPrice())
                .param("status", bill.getStatus())
                .param("createdAt", bill.getCreatedAt())
                .param("paidAt", bill.getPaidAt())
                .param("workId", bill.getWorkId())
                .update();

        return bill;
    }

    @Override
    public Optional<Bill> findById(String id) {
        String sql = "SELECT * FROM BILL WHERE Id = :id";
        return jdbcClient.sql(sql)
                .param("id", id)
                .query(this::mapRowToBill)
                .optional();
    }

    @Override
    public List<Bill> findAllByWorkId(String workId) {
        String sql = "SELECT * FROM BILL WHERE Work_id = :workId ORDER BY Step_index ASC";
        return jdbcClient.sql(sql)
                .param("workId", workId)
                .query(this::mapRowToBill)
                .list();
    }

    @Override
    public Optional<Bill> findByWorkIdAndStepIndex(String workId, int stepIndex) {
        String sql = "SELECT * FROM BILL WHERE Work_id = :workId AND Step_index = :stepIndex";
        return jdbcClient.sql(sql)
                .param("workId", workId)
                .param("stepIndex", stepIndex)
                .query(this::mapRowToBill)
                .optional();
    }

    @Override
    public int getMaxBillNumberForYear(int year) {
        String sql = """
            SELECT COALESCE(MAX(CAST(SUBSTRING(Id FROM 10) AS INTEGER)), 0)
            FROM BILL
            WHERE Id LIKE :pattern
            """;

        String pattern = "RCP-" + year + "-%";

        Integer maxNumber = jdbcClient.sql(sql)
                .param("pattern", pattern)
                .query(Integer.class)
                .single();

        return maxNumber != null ? maxNumber : 0;
    }

    @Override
    public String generateBillId(int year) {
        int nextNumber = getMaxBillNumberForYear(year) + 1;
        return String.format("RCP-%d-%03d", year, nextNumber);
    }

    private Bill mapRowToBill(ResultSet rs, int rowNum) throws SQLException {
        return Bill.builder()
                .id(rs.getString("Id"))
                .stepIndex((Integer) rs.getObject("Step_index"))
                .stepName(rs.getString("Step_name"))
                .price(rs.getBigDecimal("Price"))
                .status(rs.getString("Status"))
                .createdAt(rs.getTimestamp("Created_at") != null ? rs.getTimestamp("Created_at").toLocalDateTime() : null)
                .paidAt(rs.getTimestamp("Paid_at") != null ? rs.getTimestamp("Paid_at").toLocalDateTime() : null)
                .workId(rs.getString("Work_id"))
                .build();
    }
}

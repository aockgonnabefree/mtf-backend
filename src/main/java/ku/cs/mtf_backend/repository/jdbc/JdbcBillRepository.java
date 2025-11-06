package ku.cs.mtf_backend.repository.jdbc;

import ku.cs.mtf_backend.dto.projection.BillSummary;
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
            INSERT INTO BILL (Id, Step_index, Step_name, Price, Status, Created_at, Paid_at, Work_id,
                            PRINT_COUNT, LAST_PRINTED_AT, PRINT_STATUS)
            VALUES (:id, :stepIndex, :stepName, :price, CAST(:status AS bill_status), :createdAt, :paidAt, :workId,
                    :printCount, :lastPrintedAt, CAST(:printStatus AS bill_print_status))
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
                .param("printCount", bill.getPrintCount())
                .param("lastPrintedAt", bill.getLastPrintedAt())
                .param("printStatus", bill.getPrintStatus())
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

    @Override
    public Bill update(Bill bill) {
        String sql = """
            UPDATE BILL
            SET Step_index = :stepIndex,
                Step_name = :stepName,
                Price = :price,
                Status = CAST(:status AS bill_status),
                Created_at = :createdAt,
                Paid_at = :paidAt,
                Work_id = :workId,
                PRINT_COUNT = :printCount,
                LAST_PRINTED_AT = :lastPrintedAt,
                PRINT_STATUS = CAST(:printStatus AS bill_print_status)
            WHERE Id = :id
            """;

        int updated = jdbcClient.sql(sql)
                .param("id", bill.getId())
                .param("stepIndex", bill.getStepIndex())
                .param("stepName", bill.getStepName())
                .param("price", bill.getPrice())
                .param("status", bill.getStatus())
                .param("createdAt", bill.getCreatedAt())
                .param("paidAt", bill.getPaidAt())
                .param("workId", bill.getWorkId())
                .param("printCount", bill.getPrintCount())
                .param("lastPrintedAt", bill.getLastPrintedAt())
                .param("printStatus", bill.getPrintStatus())
                .update();

        if (updated == 0) {
            throw new RuntimeException("Bill not found with ID: " + bill.getId());
        }

        return bill;
    }

    @Override
    public long countAll() {
        String sql = "SELECT COUNT(*) FROM BILL";
        return jdbcClient.sql(sql).query(Long.class).single();
    }

    @Override
    public long countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM BILL WHERE Status = CAST(:status AS bill_status)";
        return jdbcClient.sql(sql)
                .param("status", status)
                .query(Long.class)
                .single();
    }

    @Override
    public List<BillSummary> findAllSummariesWithFilters(String employerName, String workType, String paymentStatus,
                                                          int offset, int limit) {
        StringBuilder sql = new StringBuilder("""
            SELECT
                b.Id as billId,
                CONCAT(e.Firstname, ' ', e.Lastname) as employerName,
                w.Work_type as workType,
                b.Step_index as stepIndex,
                b.Step_name as stepName,
                b.Price as price,
                b.Status as paymentStatus
            FROM BILL b
            JOIN WORK w ON b.Work_id = w.Id
            JOIN EMPLOYER e ON w.Employer_id = e.Id
            WHERE 1=1
            """);

        if (employerName != null && !employerName.isBlank()) {
            sql.append(" AND (e.Firstname ILIKE :employerName OR e.Lastname ILIKE :employerName OR e.Company_name ILIKE :employerName)");
        }
        if (workType != null && !workType.isBlank()) {
            sql.append(" AND w.Work_type = CAST(:workType AS work_type)");
        }
        if (paymentStatus != null && !paymentStatus.isBlank()) {
            sql.append(" AND b.Status = CAST(:paymentStatus AS bill_status)");
        }

        sql.append(" ORDER BY b.Created_at DESC");
        sql.append(" OFFSET :offset LIMIT :limit");

        var query = jdbcClient.sql(sql.toString())
                .param("offset", offset)
                .param("limit", limit);

        if (employerName != null && !employerName.isBlank()) {
            query = query.param("employerName", "%" + employerName + "%");
        }
        if (workType != null && !workType.isBlank()) {
            query = query.param("workType", workType);
        }
        if (paymentStatus != null && !paymentStatus.isBlank()) {
            query = query.param("paymentStatus", paymentStatus);
        }

        return query.query(this::mapRowToBillSummary).list();
    }

    @Override
    public long countWithFilters(String employerName, String workType, String paymentStatus) {
        StringBuilder sql = new StringBuilder("""
            SELECT COUNT(*) FROM BILL b
            JOIN WORK w ON b.Work_id = w.Id
            JOIN EMPLOYER e ON w.Employer_id = e.Id
            WHERE 1=1
            """);

        if (employerName != null && !employerName.isBlank()) {
            sql.append(" AND (e.Firstname ILIKE :employerName OR e.Lastname ILIKE :employerName OR e.Company_name ILIKE :employerName)");
        }
        if (workType != null && !workType.isBlank()) {
            sql.append(" AND w.Work_type = CAST(:workType AS work_type)");
        }
        if (paymentStatus != null && !paymentStatus.isBlank()) {
            sql.append(" AND b.Status = CAST(:paymentStatus AS bill_status)");
        }

        var query = jdbcClient.sql(sql.toString());

        if (employerName != null && !employerName.isBlank()) {
            query = query.param("employerName", "%" + employerName + "%");
        }
        if (workType != null && !workType.isBlank()) {
            query = query.param("workType", workType);
        }
        if (paymentStatus != null && !paymentStatus.isBlank()) {
            query = query.param("paymentStatus", paymentStatus);
        }

        return query.query(Long.class).single();
    }

    private BillSummary mapRowToBillSummary(ResultSet rs, int rowNum) throws SQLException {
        return new BillSummary(
                rs.getString("billId"),
                rs.getString("employerName"),
                rs.getString("workType"),
                (Integer) rs.getObject("stepIndex"),
                rs.getString("stepName"),
                rs.getBigDecimal("price"),
                rs.getString("paymentStatus")
        );
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
                .printCount((Integer) rs.getObject("PRINT_COUNT"))
                .lastPrintedAt(rs.getTimestamp("LAST_PRINTED_AT") != null ? rs.getTimestamp("LAST_PRINTED_AT").toLocalDateTime() : null)
                .printStatus(rs.getString("PRINT_STATUS"))
                .build();
    }
}

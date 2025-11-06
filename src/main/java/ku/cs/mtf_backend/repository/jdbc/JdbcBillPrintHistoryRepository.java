package ku.cs.mtf_backend.repository.jdbc;

import ku.cs.mtf_backend.entity.BillPrintHistory;
import ku.cs.mtf_backend.repository.BillPrintHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JdbcBillPrintHistoryRepository implements BillPrintHistoryRepository {

    private JdbcClient jdbcClient;

    @Autowired
    public JdbcBillPrintHistoryRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public BillPrintHistory save(BillPrintHistory billPrintHistory) {
        String sql = """
            INSERT INTO BILL_PRINT_HISTORY (Id, Bill_id, Print_round, Printed_at, Printed_by_agent_id, Print_reason)
            VALUES (:id, :billId, :printRound, :printedAt, :printedByAgentId, :printReason)
            """;

        jdbcClient.sql(sql)
                .param("id", billPrintHistory.getId() != null ? billPrintHistory.getId() : UUID.randomUUID().toString())
                .param("billId", billPrintHistory.getBillId())
                .param("printRound", billPrintHistory.getPrintRound())
                .param("printedAt", billPrintHistory.getPrintedAt())
                .param("printedByAgentId", billPrintHistory.getPrintedByAgentId())
                .param("printReason", billPrintHistory.getPrintReason())
                .update();

        return billPrintHistory;
    }

    @Override
    public List<BillPrintHistory> findAllByBillIdOrderByPrintRoundDesc(String billId) {
        String sql = "SELECT * FROM BILL_PRINT_HISTORY WHERE Bill_id = :billId ORDER BY Print_round DESC";
        return jdbcClient.sql(sql)
                .param("billId", billId)
                .query(this::mapRowToBillPrintHistory)
                .list();
    }

    @Override
    public Optional<BillPrintHistory> findFirstByBillIdOrderByPrintRoundDesc(String billId) {
        String sql = "SELECT * FROM BILL_PRINT_HISTORY WHERE Bill_id = :billId ORDER BY Print_round DESC LIMIT 1";
        return jdbcClient.sql(sql)
                .param("billId", billId)
                .query(this::mapRowToBillPrintHistory)
                .optional();
    }

    @Override
    public int countByBillId(String billId) {
        String sql = "SELECT COUNT(*) FROM BILL_PRINT_HISTORY WHERE Bill_id = :billId";
        Integer count = jdbcClient.sql(sql)
                .param("billId", billId)
                .query(Integer.class)
                .single();
        return count != null ? count : 0;
    }

    private BillPrintHistory mapRowToBillPrintHistory(ResultSet rs, int rowNum) throws SQLException {
        return BillPrintHistory.builder()
                .id(rs.getString("Id"))
                .billId(rs.getString("Bill_id"))
                .printRound(rs.getInt("Print_round"))
                .printedAt(rs.getTimestamp("Printed_at") != null ? rs.getTimestamp("Printed_at").toLocalDateTime() : null)
                .printedByAgentId(rs.getString("Printed_by_agent_id"))
                .printReason(rs.getString("Print_reason"))
                .build();
    }
}
package ku.cs.mtf_backend.repository;

import ku.cs.mtf_backend.entity.BillPrintHistory;

import java.util.List;
import java.util.Optional;

public interface BillPrintHistoryRepository {
    BillPrintHistory save(BillPrintHistory billPrintHistory);
    List<BillPrintHistory> findAllByBillIdOrderByPrintRoundDesc(String billId);
    Optional<BillPrintHistory> findFirstByBillIdOrderByPrintRoundDesc(String billId);
    int countByBillId(String billId);
}
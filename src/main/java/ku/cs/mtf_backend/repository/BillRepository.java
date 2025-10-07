package ku.cs.mtf_backend.repository;

import ku.cs.mtf_backend.dto.projection.BillSummary;
import ku.cs.mtf_backend.entity.Bill;

import java.util.List;
import java.util.Optional;

public interface BillRepository {
    Bill save(Bill bill);
    Bill update(Bill bill);
    Optional<Bill> findById(String id);
    List<Bill> findAllByWorkId(String workId);
    Optional<Bill> findByWorkIdAndStepIndex(String workId, int stepIndex);
    int getMaxBillNumberForYear(int year);
    String generateBillId(int year);

    // Statistics
    long countAll();
    long countByStatus(String status);

    // List with filters and pagination
    List<BillSummary> findAllSummariesWithFilters(String employerName, String workType, String paymentStatus,
                                                   int offset, int limit);
    long countWithFilters(String employerName, String workType, String paymentStatus);
}

package ku.cs.mtf_backend.repository;

import ku.cs.mtf_backend.entity.Bill;

import java.util.List;
import java.util.Optional;

public interface BillRepository {
    Bill save(Bill bill);
    Optional<Bill> findById(String id);
    List<Bill> findAllByWorkId(String workId);
    Optional<Bill> findByWorkIdAndStepIndex(String workId, int stepIndex);
    int getMaxBillNumberForYear(int year);
    String generateBillId(int year);
}

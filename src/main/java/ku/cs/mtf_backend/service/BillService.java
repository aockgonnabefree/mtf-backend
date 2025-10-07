package ku.cs.mtf_backend.service;

import ku.cs.mtf_backend.dto.projection.BillSummary;
import ku.cs.mtf_backend.dto.response.BillStatisticsResponse;
import ku.cs.mtf_backend.dto.response.PageResponse;
import ku.cs.mtf_backend.entity.Bill;
import ku.cs.mtf_backend.exception.ResourceNotFoundException;
import ku.cs.mtf_backend.repository.BillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BillService {

    private final BillRepository billRepository;

    @Autowired
    public BillService(BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    public Bill getBillById(String billId) {
        return billRepository.findById(billId)
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found with ID: " + billId));
    }

    public List<Bill> getBillsByWorkId(String workId) {
        return billRepository.findAllByWorkId(workId);
    }

    public Bill getBillByWorkIdAndStepIndex(String workId, int stepIndex) {
        return billRepository.findByWorkIdAndStepIndex(workId, stepIndex)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Bill not found for work ID: " + workId + " and step index: " + stepIndex));
    }

    public Bill markBillAsPaid(String billId) {
        Bill bill = getBillById(billId);

        if ("PAID".equals(bill.getStatus())) {
            throw new IllegalStateException("Bill is already paid");
        }

        Bill updatedBill = Bill.builder()
                .id(bill.getId())
                .stepIndex(bill.getStepIndex())
                .stepName(bill.getStepName())
                .price(bill.getPrice())
                .status("PAID")
                .createdAt(bill.getCreatedAt())
                .paidAt(LocalDateTime.now())
                .workId(bill.getWorkId())
                .build();

        return billRepository.update(updatedBill);
    }

    public BillStatisticsResponse getStatistics() {
        long total = billRepository.countAll();
        long paid = billRepository.countByStatus("PAID");
        long unpaid = billRepository.countByStatus("NOT_PAID");

        return BillStatisticsResponse.builder()
                .totalBills(total)
                .paidBills(paid)
                .unpaidBills(unpaid)
                .build();
    }

    public PageResponse<BillSummary> getBillsWithPagination(Integer page, Integer size,
                                                             String employerName, String workType, String paymentStatus) {
        if (page < 0) {
            throw new IllegalArgumentException("Page number cannot be negative");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Page size must be greater than 0");
        }

        int offset = page * size;
        List<BillSummary> bills = billRepository.findAllSummariesWithFilters(employerName, workType, paymentStatus, offset, size);
        long totalElements = billRepository.countWithFilters(employerName, workType, paymentStatus);
        int totalPages = (int) Math.ceil((double) totalElements / size);

        return PageResponse.<BillSummary>builder()
                .content(bills)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .currentPage(page)
                .pageSize(size)
                .build();
    }
}

package ku.cs.mtf_backend.service;

import ku.cs.mtf_backend.dto.projection.BillSummary;
import ku.cs.mtf_backend.dto.request.PrintBillRequest;
import ku.cs.mtf_backend.dto.response.BillPrintHistoryDTO;
import ku.cs.mtf_backend.dto.response.BillPrintResponse;
import ku.cs.mtf_backend.dto.response.BillStatisticsResponse;
import ku.cs.mtf_backend.dto.response.PageResponse;
import ku.cs.mtf_backend.entity.Agent;
import ku.cs.mtf_backend.entity.Bill;
import ku.cs.mtf_backend.entity.BillPrintHistory;
import ku.cs.mtf_backend.exception.ResourceNotFoundException;
import ku.cs.mtf_backend.repository.AgentRepository;
import ku.cs.mtf_backend.repository.BillPrintHistoryRepository;
import ku.cs.mtf_backend.repository.BillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class BillService {

    private final BillRepository billRepository;
    private final BillPrintHistoryRepository billPrintHistoryRepository;
    private final AgentRepository agentRepository;

    @Autowired
    public BillService(BillRepository billRepository,
                      BillPrintHistoryRepository billPrintHistoryRepository,
                      AgentRepository agentRepository) {
        this.billRepository = billRepository;
        this.billPrintHistoryRepository = billPrintHistoryRepository;
        this.agentRepository = agentRepository;
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
                .printCount(bill.getPrintCount())
                .lastPrintedAt(bill.getLastPrintedAt())
                .printStatus(bill.getPrintStatus())
                .build();

        return billRepository.update(updatedBill);
    }

    public BillPrintResponse printBill(String billId, String agentId, PrintBillRequest request) {
        // 1. ตรวจสอบว่า bill มีอยู่จริง
        Bill bill = getBillById(billId);

        // 2. ตรวจสอบว่า agent มีอยู่จริง
        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new ResourceNotFoundException("Agent not found with ID: " + agentId));

        // 3. นับจำนวนครั้งที่พิมพ์แล้ว
        int currentPrintCount = billPrintHistoryRepository.countByBillId(billId);
        int newPrintRound = currentPrintCount + 1;

        // 4. สร้างประวัติการพิมพ์
        BillPrintHistory printHistory = BillPrintHistory.builder()
                .id(UUID.randomUUID().toString())
                .billId(billId)
                .printRound(newPrintRound)
                .printedAt(LocalDateTime.now())
                .printedByAgentId(agentId)
                .printReason(request != null ? request.getReason() : null)
                .build();

        billPrintHistoryRepository.save(printHistory);

        // 5. อัพเดท bill ใหม่
        String newPrintStatus = newPrintRound == 1 ? "PRINTED" : "REPRINTED";
        Bill updatedBill = Bill.builder()
                .id(bill.getId())
                .stepIndex(bill.getStepIndex())
                .stepName(bill.getStepName())
                .price(bill.getPrice())
                .status(bill.getStatus())
                .createdAt(bill.getCreatedAt())
                .paidAt(bill.getPaidAt())
                .workId(bill.getWorkId())
                .printCount(newPrintRound)
                .lastPrintedAt(printHistory.getPrintedAt())
                .printStatus(newPrintStatus)
                .build();

        billRepository.update(updatedBill);

        // 6. Return response
        return BillPrintResponse.builder()
                .bill(updatedBill)
                .printRound(newPrintRound)
                .printedAt(printHistory.getPrintedAt())
                .printedByAgentName(agent.getFirstname() + " " + agent.getLastname())
                .printReason(printHistory.getPrintReason())
                .build();
    }

    public List<BillPrintHistoryDTO> getPrintHistory(String billId) {
        // 1. ตรวจสอบว่า bill มีอยู่จริง
        getBillById(billId);

        // 2. ดึงประวัติการพิมพ์ทั้งหมด
        List<BillPrintHistory> printHistoryList = billPrintHistoryRepository.findAllByBillIdOrderByPrintRoundDesc(billId);

        // 3. แปลงเป็น DTO พร้อมชื่อ agent
        return printHistoryList.stream()
                .map(history -> {
                    Agent agent = agentRepository.findById(history.getPrintedByAgentId())
                            .orElse(null);
                    String agentName = agent != null ? agent.getFirstname() + " " + agent.getLastname() : "Unknown Agent";

                    return BillPrintHistoryDTO.builder()
                            .printRound(history.getPrintRound())
                            .printedAt(history.getPrintedAt())
                            .printedByAgentName(agentName)
                            .printReason(history.getPrintReason())
                            .build();
                })
                .toList();
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

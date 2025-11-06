package ku.cs.mtf_backend.controller;

import ku.cs.mtf_backend.dto.projection.BillSummary;
import ku.cs.mtf_backend.dto.request.PrintBillRequest;
import ku.cs.mtf_backend.dto.response.BillPrintHistoryDTO;
import ku.cs.mtf_backend.dto.response.BillPrintResponse;
import ku.cs.mtf_backend.dto.response.BillStatisticsResponse;
import ku.cs.mtf_backend.dto.response.PageResponse;
import ku.cs.mtf_backend.entity.Bill;
import ku.cs.mtf_backend.entity.Work;
import ku.cs.mtf_backend.exception.ResourceNotFoundException;
import ku.cs.mtf_backend.service.BillService;
import ku.cs.mtf_backend.service.WorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/bills")
public class BillController {

    private final BillService billService;
    private final WorkService workService;

    @Autowired
    public BillController(BillService billService, WorkService workService) {
        this.billService = billService;
        this.workService = workService;
    }

    @GetMapping("/statistics")
    public ResponseEntity<BillStatisticsResponse> getStatistics() {
        BillStatisticsResponse statistics = billService.getStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping
    public ResponseEntity<?> getAllBills(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer size,
            @RequestParam(required = false) String employerName,
            @RequestParam(required = false) String workType,
            @RequestParam(required = false) String paymentStatus) {
        try {
            PageResponse<BillSummary> response = billService.getBillsWithPagination(page, size, employerName, workType, paymentStatus);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{billId}")
    public ResponseEntity<?> getBillById(@PathVariable String billId) {
        try {
            Bill bill = billService.getBillById(billId);
            return ResponseEntity.ok(bill);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/details")
    public ResponseEntity<?> getBillsByWork(
            @RequestParam String workId,
            @RequestParam(required = false) Integer stepIndex) {
        try {
            if (stepIndex != null) {
                Bill bill = billService.getBillByWorkIdAndStepIndex(workId, stepIndex);
                return ResponseEntity.ok(bill);
            }

            List<Bill> bills = billService.getBillsByWorkId(workId);
            return ResponseEntity.ok(bills);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{billId}/pay")
    public ResponseEntity<?> payBillAndAdvanceWork(@PathVariable String billId) {
        try {
            // 1. Mark bill as paid
            Bill paidBill = billService.markBillAsPaid(billId);

            // 2. Get work ID from the bill
            String workId = paidBill.getWorkId();
            if (workId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Bill is not associated with any work"));
            }

            // 3. Check if this is the last step before advancing
            Work currentWork = workService.getWorkById(workId);
            boolean isLastStep = workService.isLastStep(workId);

            // 4. If not last step, advance work to next step (creates new bill automatically)
            Work updatedWork;
            if (!isLastStep) {
                updatedWork = workService.advanceToNextStep(workId);
            } else {
                // Mark work as finished
                updatedWork = workService.markWorkAsFinished(workId);
            }

            return ResponseEntity.ok(Map.of(
                    "bill", paidBill,
                    "work", updatedWork
            ));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{billId}/print")
    public ResponseEntity<?> printBill(@PathVariable String billId,
                                     @RequestBody PrintBillRequest request) {
        try {
            // Validate agentId
            if (request == null || request.getAgentId() == null || request.getAgentId().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Agent ID is required"));
            }

            BillPrintResponse response = billService.printBill(billId, request.getAgentId(), request);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to print bill: " + e.getMessage()));
        }
    }

    @GetMapping("/{billId}/print-history")
    public ResponseEntity<?> getPrintHistory(@PathVariable String billId) {
        try {
            List<BillPrintHistoryDTO> history = billService.getPrintHistory(billId);
            return ResponseEntity.ok(Map.of("printHistory", history));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to get print history: " + e.getMessage()));
        }
    }
}

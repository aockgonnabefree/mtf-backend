package ku.cs.mtf_backend.controller;

import ku.cs.mtf_backend.entity.Bill;
import ku.cs.mtf_backend.exception.ResourceNotFoundException;
import ku.cs.mtf_backend.service.BillService;
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

    @Autowired
    public BillController(BillService billService) {
        this.billService = billService;
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

    @GetMapping
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
}

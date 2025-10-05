package ku.cs.mtf_backend.controller;

import jakarta.validation.Valid;
import ku.cs.mtf_backend.dto.request.CreateEmploymentContractPayload;
import ku.cs.mtf_backend.dto.response.EmploymentContractHistoryDTO;
import ku.cs.mtf_backend.dto.response.EmploymentContractResponse;
import ku.cs.mtf_backend.entity.EmploymentContract;
import ku.cs.mtf_backend.exception.ResourceNotFoundException;
import ku.cs.mtf_backend.service.EmploymentContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/employment-contracts")
public class EmploymentContractController {

    private EmploymentContractService employmentContractService;

    @Autowired
    public EmploymentContractController(EmploymentContractService employmentContractService) {
        this.employmentContractService = employmentContractService;
    }

    @PostMapping
    public ResponseEntity<?> createEmploymentContract(@Valid @RequestBody CreateEmploymentContractPayload payload) {
        try {
            EmploymentContract createdContract = employmentContractService.createEmploymentContract(payload);
            Map<String, Object> response = Map.of(
                    "message", "Employment contract created successfully.",
                    "contractId", createdContract.getId()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEmploymentContractById(@PathVariable String id) {
        try {
            EmploymentContractResponse response = employmentContractService.getEmploymentContractById(id);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/history")
    public ResponseEntity<?> getEmploymentContractHistory(
            @RequestParam String passportNo,
            @RequestParam String employerId,
            @RequestParam(defaultValue = "3") Integer limit) {
        try {
            List<EmploymentContractHistoryDTO> history = employmentContractService.getEmploymentContractHistory(passportNo, employerId, limit);
            return ResponseEntity.ok(history);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve employment contract history: " + e.getMessage()));
        }
    }
}

package ku.cs.mtf_backend.controller;

import jakarta.validation.Valid;
import ku.cs.mtf_backend.dto.request.CreateEmployerPayload;
import ku.cs.mtf_backend.dto.request.UpdateEmployerPayload;
import ku.cs.mtf_backend.dto.response.EmployerStatisticsResponse;
import ku.cs.mtf_backend.dto.response.EmployerSummaryDTO;
import ku.cs.mtf_backend.dto.response.PageResponse;
import ku.cs.mtf_backend.entity.Employer;
import ku.cs.mtf_backend.exception.ResourceNotFoundException;
import ku.cs.mtf_backend.service.EmployerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/employers")
public class EmployerController {
    private final EmployerService employerService;

    @Autowired
    public EmployerController(EmployerService employerService) {
        this.employerService = employerService;
    }

    @PostMapping
    public ResponseEntity<?> createEmployer(@Valid @RequestBody CreateEmployerPayload payload) {
        try {
            Employer createdEmployer = employerService.createEmployer(payload);
            Map<String, Object> response = Map.of(
                    "message", "Employer created successfully.",
                    "employerId", createdEmployer.getId()
            );
            // Return 201 Created status on success
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            // Return 409 Conflict status if there is a duplicate entry
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{employerId}")
    public ResponseEntity<?> updateEmployer(
            @PathVariable String employerId,
            @Valid @RequestBody UpdateEmployerPayload payload) {
        try {
            Employer updatedEmployer = employerService.updateEmployer(employerId, payload);
            Map<String, Object> response = Map.of(
                    "message", "Employer updated successfully.",
                    "employerId", updatedEmployer.getId()
            );
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            // Catches the exception from the service if the employer is not found.
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            // Catches the exception for duplicate email/phone on another employer.
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/statistics")
    public ResponseEntity<EmployerStatisticsResponse> getStatistics() {
        EmployerStatisticsResponse statistics = employerService.getStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping
    public ResponseEntity<?> getEmployers(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer size,
            @RequestParam(required = false) String nameContains,
            @RequestParam(required = false) String status) {
        try {
            PageResponse<EmployerSummaryDTO> response = employerService.getEmployersWithPagination(page, size, nameContains, status);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
}

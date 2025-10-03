package ku.cs.mtf_backend.controller;

import jakarta.validation.Valid;
import ku.cs.mtf_backend.dto.request.CreateEmployerPayload;
import ku.cs.mtf_backend.entity.Employer;
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
}

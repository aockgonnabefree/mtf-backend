package ku.cs.mtf_backend.controller;

import jakarta.validation.Valid;
import ku.cs.mtf_backend.dto.request.CreateWp46Payload;
import ku.cs.mtf_backend.dto.response.Wp46HistoryDTO;
import ku.cs.mtf_backend.dto.response.Wp46Response;
import ku.cs.mtf_backend.entity.Wp46;
import ku.cs.mtf_backend.exception.ResourceNotFoundException;
import ku.cs.mtf_backend.service.Wp46Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/wp46")
public class Wp46Controller {

    private Wp46Service wp46Service;

    @Autowired
    public Wp46Controller(Wp46Service wp46Service) {
        this.wp46Service = wp46Service;
    }

    @PostMapping
    public ResponseEntity<?> createWp46(@Valid @RequestBody CreateWp46Payload payload) {
        try {
            Wp46 createdWp46 = wp46Service.createWp46(payload);
            Map<String, Object> response = Map.of(
                    "message", "WP-46 document created successfully.",
                    "documentId", createdWp46.getId()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getWp46ById(@PathVariable String id) {
        try {
            Wp46Response response = wp46Service.getWp46ById(id);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/history")
    public ResponseEntity<?> getWp46History(
            @RequestParam String passportNo,
            @RequestParam String employerId,
            @RequestParam(defaultValue = "3") Integer limit) {
        try {
            List<Wp46HistoryDTO> history = wp46Service.getWp46History(passportNo, employerId, limit);
            return ResponseEntity.ok(history);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve WP-46 history: " + e.getMessage()));
        }
    }
}

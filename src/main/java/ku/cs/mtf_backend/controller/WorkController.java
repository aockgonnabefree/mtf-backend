package ku.cs.mtf_backend.controller;

import jakarta.validation.Valid;
import ku.cs.mtf_backend.dto.projection.WorkSummary;
import ku.cs.mtf_backend.dto.request.CreateWorkPayload;
import ku.cs.mtf_backend.dto.response.PageResponse;
import ku.cs.mtf_backend.dto.response.WorkDetailResponse;
import ku.cs.mtf_backend.dto.response.WorkStatisticsResponse;
import ku.cs.mtf_backend.entity.Work;
import ku.cs.mtf_backend.exception.ResourceNotFoundException;
import ku.cs.mtf_backend.service.WorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/works")
public class WorkController {

    private WorkService workService;

    @Autowired
    public WorkController(WorkService workService) {
        this.workService = workService;
    }

    @PostMapping
    public ResponseEntity<?> createWork(@Valid @RequestBody CreateWorkPayload payload) {
        try {
            Work createdWork = workService.createWork(payload);
            Map<String, Object> response = Map.of(
                    "message", "Work created successfully.",
                    "workId", createdWork.getId()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/statistics")
    public ResponseEntity<WorkStatisticsResponse> getStatistics() {
        WorkStatisticsResponse statistics = workService.getStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping
    public ResponseEntity<?> getWorks(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer size,
            @RequestParam(required = false) String employerName,
            @RequestParam(required = false) String workType,
            @RequestParam(required = false) String status) {
        try {
            PageResponse<WorkSummary> response = workService.getWorksWithPagination(page, size, employerName, workType, status);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{workId}")
    public ResponseEntity<?> getWorkById(@PathVariable String workId) {
        try {
            WorkDetailResponse response = workService.getWorkDetailById(workId);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
}

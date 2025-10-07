package ku.cs.mtf_backend.controller;

import jakarta.validation.Valid;
import ku.cs.mtf_backend.dto.projection.AgentSummary;
import ku.cs.mtf_backend.dto.request.CreateAgentPayload;
import ku.cs.mtf_backend.dto.response.AgentCreationResponse;
import ku.cs.mtf_backend.dto.response.AgentStatisticsResponse;
import ku.cs.mtf_backend.dto.response.PageResponse;
import ku.cs.mtf_backend.service.AgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/agents")
public class AgentController {

    private final AgentService agentService;

    @Autowired
    public AgentController(AgentService agentService) {
        this.agentService = agentService;
    }

    @GetMapping("/statistics")
    public ResponseEntity<AgentStatisticsResponse> getStatistics() {
        AgentStatisticsResponse statistics = agentService.getStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping
    public ResponseEntity<?> getAgents(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String status) {
        try {
            PageResponse<AgentSummary> response = agentService.getAgentsWithPagination(page, size, fullName, status);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createAgent(@Valid @RequestBody CreateAgentPayload payload) {
        try {
            AgentCreationResponse result = agentService.createAgent(payload);

            Map<String, Object> response = Map.of(
                    "message", "Agent created successfully.",
                    "agentId", result.getAgent().getId(),
                    "password", result.getPlainPassword()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }
}

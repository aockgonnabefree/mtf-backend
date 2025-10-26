package ku.cs.mtf_backend.service;

import ku.cs.mtf_backend.dto.projection.AgentSummary;
import ku.cs.mtf_backend.dto.request.CreateAgentPayload;
import ku.cs.mtf_backend.dto.request.UpdateAgentPayload;
import ku.cs.mtf_backend.dto.response.AgentCreationResponse;
import ku.cs.mtf_backend.dto.response.AgentDetailResponse;
import ku.cs.mtf_backend.dto.response.AgentStatisticsResponse;
import ku.cs.mtf_backend.dto.response.PageResponse;
import ku.cs.mtf_backend.entity.Address;
import ku.cs.mtf_backend.entity.Agent;
import ku.cs.mtf_backend.exception.ResourceNotFoundException;
import ku.cs.mtf_backend.repository.AddressRepository;
import ku.cs.mtf_backend.repository.AgentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AgentService {

    private final AgentRepository agentRepository;
    private final AddressService addressService;
    private final AddressRepository addressRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AgentService(AgentRepository agentRepository,
                        AddressService addressService,
                        AddressRepository addressRepository,
                        PasswordEncoder passwordEncoder) {
        this.agentRepository = agentRepository;
        this.addressService = addressService;
        this.addressRepository = addressRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AgentCreationResponse createAgent(CreateAgentPayload payload) {
        // 1. Validate for duplicate agent
        if (agentRepository.existsById(payload.getId())) {
            throw new IllegalArgumentException("Agent with ID " + payload.getId() + " already exists.");
        }
        if (agentRepository.existsByEmail(payload.getEmail())) {
            throw new IllegalArgumentException("Email " + payload.getEmail() + " is already in use.");
        }

        // 2. Generate password (mtf001, mtf002, etc.) - ONLY ONCE
        String plainPassword = agentRepository.generateNextAgentPassword();
        String hashedPassword = passwordEncoder.encode(plainPassword);

        // 3. Handle address
        Address address = addressService.findOrCreateAddress(payload.getAddress());

        // 4. Build Agent entity
        Agent agentToSave = Agent.builder()
                .id(payload.getId())
                .firstname(payload.getFirstName())
                .lastname(payload.getLastName())
                .email(payload.getEmail())
                .hashedPassword(hashedPassword)
                .status(payload.getStatus())
                .role("AGENT") // Automatically set role to AGENT for agent creation
                .addressId(address.getId())
                .build();

        // 5. Save agent
        Agent savedAgent = agentRepository.save(agentToSave);

        // 6. Return both agent and plain password
        return new AgentCreationResponse(savedAgent, plainPassword);
    }

    public AgentDetailResponse getAgentById(String agentId) {
        // 1. ค้นหา Agent
        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new ResourceNotFoundException("Agent not found with id: " + agentId));

        // 2. ค้นหา Address
        Address address = addressRepository.findById(agent.getAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + agent.getAddressId()));

        // 3. Build response
        return AgentDetailResponse.builder()
                .id(agent.getId())
                .firstname(agent.getFirstname())
                .lastname(agent.getLastname())
                .email(agent.getEmail())
                .status(agent.getStatus())
                .role(agent.getRole())
                .address(AgentDetailResponse.AddressResponse.builder()
                        .id(address.getId())
                        .addrDetailTh(address.getAddrDetailTh())
                        .subDistrictTh(address.getSubDistrictTh())
                        .districtTh(address.getDistrictTh())
                        .provinceTh(address.getProvinceTh())
                        .postalCode(address.getPostalCode())
                        .build())
                .build();
    }

    public AgentStatisticsResponse getStatistics() {
        long total = agentRepository.countAll();
        long active = agentRepository.countByStatus("ACTIVE");
        long inactive = agentRepository.countByStatus("INACTIVE");

        return AgentStatisticsResponse.builder()
                .totalAgents(total)
                .activeAgents(active)
                .inactiveAgents(inactive)
                .build();
    }

    public PageResponse<AgentSummary> getAgentsWithPagination(Integer page, Integer size,
                                                               String fullName, String status) {
        if (page < 0) {
            throw new IllegalArgumentException("Page number cannot be negative");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Page size must be greater than 0");
        }

        int offset = page * size;
        List<AgentSummary> agents = agentRepository.findAllSummariesWithFilters(fullName, status, offset, size);
        long totalElements = agentRepository.countWithFilters(fullName, status);
        int totalPages = (int) Math.ceil((double) totalElements / size);

        return PageResponse.<AgentSummary>builder()
                .content(agents)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .currentPage(page)
                .pageSize(size)
                .build();
    }

    @Transactional
    public Agent updateAgent(String agentId, UpdateAgentPayload payload) {
        // 1. Find existing agent
        Agent existingAgent = agentRepository.findById(agentId)
                .orElseThrow(() -> new ResourceNotFoundException("Agent not found with ID: " + agentId));

        // 2. Check if email is being changed and if it's already in use by another agent
        if (!existingAgent.getEmail().equals(payload.getEmail())) {
            if (agentRepository.existsByEmail(payload.getEmail())) {
                throw new IllegalArgumentException("Email " + payload.getEmail() + " is already in use.");
            }
        }

        // 3. Handle address update
        Address address = addressService.findOrCreateAddress(payload.getAddress());

        // 4. Build updated agent (keep password unchanged, role unchanged)
        Agent updatedAgent = Agent.builder()
                .id(agentId)
                .firstname(payload.getFirstName())
                .lastname(payload.getLastName())
                .email(payload.getEmail())
                .hashedPassword(existingAgent.getHashedPassword()) // Keep existing password
                .status(payload.getStatus())
                .role(existingAgent.getRole()) // Keep existing role (cannot be changed through update)
                .addressId(address.getId())
                .build();

        // 5. Update agent
        return agentRepository.update(updatedAgent);
    }
}

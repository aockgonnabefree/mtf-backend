package ku.cs.mtf_backend.service;

import ku.cs.mtf_backend.dto.request.CreateAgentPayload;
import ku.cs.mtf_backend.dto.response.AgentCreationResponse;
import ku.cs.mtf_backend.entity.Address;
import ku.cs.mtf_backend.entity.Agent;
import ku.cs.mtf_backend.repository.AgentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AgentService {

    private final AgentRepository agentRepository;
    private final AddressService addressService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AgentService(AgentRepository agentRepository,
                        AddressService addressService,
                        PasswordEncoder passwordEncoder) {
        this.agentRepository = agentRepository;
        this.addressService = addressService;
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
                .addressId(address.getId())
                .build();

        // 5. Save agent
        Agent savedAgent = agentRepository.save(agentToSave);

        // 6. Return both agent and plain password
        return new AgentCreationResponse(savedAgent, plainPassword);
    }
}

package ku.cs.mtf_backend.controller;

import ku.cs.mtf_backend.dto.request.CreateAdminPayload;
import ku.cs.mtf_backend.dto.request.LoginRequest;
import ku.cs.mtf_backend.dto.response.AdminCreationResponse;
import ku.cs.mtf_backend.dto.response.LoginResponse;
import ku.cs.mtf_backend.entity.Agent;
import ku.cs.mtf_backend.entity.Address;
import ku.cs.mtf_backend.repository.AgentRepository;
import ku.cs.mtf_backend.repository.AddressRepository;
import ku.cs.mtf_backend.service.AddressService;
import ku.cs.mtf_backend.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AgentRepository agentRepository;
    private final PasswordEncoder passwordEncoder;
    private final AddressService addressService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService,
                       AgentRepository agentRepository, PasswordEncoder passwordEncoder,
                       AddressService addressService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.agentRepository = agentRepository;
        this.passwordEncoder = passwordEncoder;
        this.addressService = addressService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Agent agent = agentRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Agent not found"));

        String token = jwtService.generateToken(userDetails);

        LoginResponse response = new LoginResponse(
                token,
                agent.getId(),
                agent.getFirstname() + " " + agent.getLastname(),
                agent.getEmail(),
                agent.getRole(),
                agent.getStatus()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/create-admin")
    public ResponseEntity<AdminCreationResponse> createFirstAdmin(@RequestBody CreateAdminPayload payload) {
        // Check if any admin already exists
        if (agentRepository.existsByRole("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new AdminCreationResponse(
                            "Admin account already exists. Use authenticated endpoint to create additional admins.",
                            null, null, null, null, null
                    ));
        }

        // Validate for duplicate agent
        if (agentRepository.existsByEmail(payload.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(new AdminCreationResponse(
                            "Email " + payload.getEmail() + " is already in use.",
                            null, null, null, null, null
                    ));
        }

        // Handle address
        Address address = addressService.findOrCreateAddress(payload.getAddress());

        // Hash the provided password
        String hashedPassword = passwordEncoder.encode(payload.getPassword());

        // Build admin agent
        Agent adminAgent = Agent.builder()
                .id("ADMIN001")
                .firstname(payload.getFirstName())
                .lastname(payload.getLastName())
                .email(payload.getEmail())
                .hashedPassword(hashedPassword)
                .status("ACTIVE")
                .role("ADMIN") // Automatically set role to ADMIN for admin creation
                .addressId(address.getId())
                .build();

        // Save admin
        Agent savedAdmin = agentRepository.save(adminAgent);

        AdminCreationResponse response = new AdminCreationResponse(
                "First admin account created successfully.",
                savedAdmin.getId(),
                savedAdmin.getFirstname() + " " + savedAdmin.getLastname(),
                savedAdmin.getEmail(),
                savedAdmin.getRole(),
                savedAdmin.getStatus()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
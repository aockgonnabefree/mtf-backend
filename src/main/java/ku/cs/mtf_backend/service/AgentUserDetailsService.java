package ku.cs.mtf_backend.service;

import ku.cs.mtf_backend.repository.AgentRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class AgentUserDetailsService implements UserDetailsService {

    private final AgentRepository agentRepository;

    public AgentUserDetailsService(AgentRepository agentRepository) {
        this.agentRepository = agentRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return agentRepository.findByEmail(email)
                .map(agent -> User.builder()
                        .username(agent.getEmail())
                        .password(agent.getHashedPassword())
                        .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + agent.getRole())))
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Agent not found with email: " + email));
    }
}
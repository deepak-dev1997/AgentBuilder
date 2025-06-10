package com.agentbuilder.security.controller;

import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.agentbuilder.security.enums.Role;
import com.agentbuilder.security.models.User;
import com.agentbuilder.security.repository.UserRepository;
import com.agentbuilder.security.service.JwtService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    /* ---------- DTOs ---------- */
    public record AuthRequest(String username, String password) {}
    public record AuthResponse(String token) {}

    @PostMapping("/register")
    public AuthResponse register(@RequestBody AuthRequest req) {
        if (repo.existsByUsername(req.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already taken");
        }
        User user = User.builder()
                .username(req.username())
                .password(encoder.encode(req.password()))
                .roles(Set.of(Role.USER))
                .build();
        repo.save(user);
        return new AuthResponse(jwt.generate(user));
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest req) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(req.username(), req.password());
        authManager.authenticate(authToken);       // throws if bad credentials
        User user = repo.findByUsername(req.username()).orElseThrow();
        return new AuthResponse(jwt.generate(user));
    }
    
    @GetMapping("/me")
    public User me(Authentication auth) {
        // Spring Security populates Authentication if JWT filter succeeded
        return repo.findByUsername(auth.getName()).orElseThrow();
    }
}

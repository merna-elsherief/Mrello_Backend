package com.example.mrellobackend.auth;

import com.example.mrellobackend.auth.config.JwtService;
import com.example.mrellobackend.auth.user.User;
import com.example.mrellobackend.auth.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        // Create user from request
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build();

        userRepository.save(user);

        // Generate JWT token
        String token = jwtService.generateToken(user);
        System.out.println("Generated Token: " + token);

        return AuthenticationResponse.builder()
                .token(token)
                .build();
    }

    public AuthenticationResponse login(AuthenticationRequest request) {
        // step 1: verify credentials
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // step 2: fetch user
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // step 3: generate token
        String token = jwtService.generateToken(user);
        System.out.println("Generated Token: " + token);

        return AuthenticationResponse.builder()
                .token(token)
                .build();
    }
}


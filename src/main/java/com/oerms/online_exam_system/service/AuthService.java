package com.oerms.online_exam_system.service;

import com.oerms.online_exam_system.dto.AuthResponse;
import com.oerms.online_exam_system.dto.LoginRequest;
import com.oerms.online_exam_system.entity.User;
import com.oerms.online_exam_system.repository.UserRepository;
import com.oerms.online_exam_system.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * AuthService — handles the authentication business logic.
 *
 * register(): Encodes the password with BCrypt and persists the user.
 * login():    Delegates credential verification to AuthenticationManager,
 *             then generates and returns a JWT token.
 *
 * Constructor injection is used throughout (no @Autowired field injection).
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager,
                       UserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    // ─── Register ────────────────────────────────────────────────────────────

    /**
     * Registers a new user.
     * The plain-text password is encoded with BCrypt before being saved.
     */
    public AuthResponse register(User user) {
        // Never store plain-text passwords
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);

        // Load the saved user as UserDetails to generate the token
        UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getEmail());
        String token = jwtService.generateToken(userDetails);

        return new AuthResponse(token, savedUser.getEmail(), savedUser.getRole().name(),
                "User registered successfully");
    }

    // ─── Login ───────────────────────────────────────────────────────────────

    /**
     * Authenticates an existing user.
     * Spring's AuthenticationManager verifies the credentials against the DB.
     * Throws BadCredentialsException if email/password don't match.
     */
    public AuthResponse login(LoginRequest request) {
        // This internally calls CustomUserDetailsService + BCrypt comparison
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtService.generateToken(userDetails);

        // Fetch user entity to include the role in the response
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        return new AuthResponse(token, user.getEmail(), user.getRole().name(),
                "Login successful");
    }
}

package com.oerms.online_exam_system.config;

import com.oerms.online_exam_system.security.CustomUserDetailsService;
import com.oerms.online_exam_system.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * SecurityConfig — central Spring Security configuration.
 *
 * Key decisions:
 *  - Sessions are STATELESS (JWT replaces server-side sessions).
 *  - CSRF is disabled (safe for stateless REST APIs).
 *  - Public routes: /api/auth/** (register + login).
 *  - /api/teacher/** requires ROLE_TEACHER.
 *  - /api/student/** requires ROLE_STUDENT.
 *  - All other requests require authentication.
 *  - The JwtAuthenticationFilter runs before UsernamePasswordAuthenticationFilter.
 *  - BCryptPasswordEncoder is registered as a Bean for password hashing.
 *  - @EnableMethodSecurity enables @PreAuthorize / @PostAuthorize on methods.
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter,
                          CustomUserDetailsService userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    // ─── Security Filter Chain ───────────────────────────────────────────────

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF — not needed for stateless JWT APIs
                .csrf(AbstractHttpConfigurer::disable)

                // URL-based authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints — no token required
                        .requestMatchers("/api/auth/**").permitAll()

                        // Role-restricted endpoints
                        .requestMatchers("/api/teacher/**").hasRole("TEACHER")
                        .requestMatchers("/api/student/**").hasRole("STUDENT")

                        // All other endpoints require a valid token
                        .anyRequest().authenticated()
                )

                // Stateless session — Spring Security will NOT create HTTP sessions
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Use our DaoAuthenticationProvider (BCrypt + CustomUserDetailsService)
                .authenticationProvider(authenticationProvider())

                // Insert the JWT filter before the default username/password filter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ─── Authentication Provider ─────────────────────────────────────────────

    /**
     * Wires together:
     *  - CustomUserDetailsService  (load user by email)
     *  - BCryptPasswordEncoder     (verify hashed password)
     *
     * Spring Security 6.5+ (Spring Boot 4.x): DaoAuthenticationProvider requires
     * UserDetailsService as a constructor argument — no-arg constructor was removed.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // ─── Authentication Manager ──────────────────────────────────────────────

    /**
     * Exposes the AuthenticationManager as a Bean so AuthService can inject it.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // ─── Password Encoder ────────────────────────────────────────────────────

    /**
     * BCryptPasswordEncoder — the industry-standard password hashing algorithm.
     * Passwords are NEVER stored as plain text.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

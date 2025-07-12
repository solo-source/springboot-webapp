package com.stackit.config;

import com.stackit.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(authProvider);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Keep disabled for simplicity, enable/configure for production
            .authorizeHttpRequests(authorize -> authorize
                // Allow static resources (CSS, JS, images)
                .requestMatchers(
                    AntPathRequestMatcher.antMatcher("/css/**"),
                    AntPathRequestMatcher.antMatcher("/js/**"),
                    AntPathRequestMatcher.antMatcher("/images/**")
                ).permitAll()
                // Allow the homepage ('/') and the combined auth page ('/auth')
                .requestMatchers(
                    AntPathRequestMatcher.antMatcher("/"),
                    AntPathRequestMatcher.antMatcher("/auth**") // Handles /auth and /auth?param
                ).permitAll()
                // Allow viewing questions (and answers) without login
                // If you want all /questions paths to require auth, remove this
                .requestMatchers(AntPathRequestMatcher.antMatcher("/questions")).permitAll()
                // If you want specific /questions/{id} to be public
                .requestMatchers(AntPathRequestMatcher.antMatcher("/questions/{id}")).permitAll()
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            // Configure form-based login to use the combined auth page
            .formLogin(form -> form
                .loginPage("/auth") // Custom login page URL
                .loginProcessingUrl("/authenticate") // Spring Security's default URL for login POST
                .defaultSuccessUrl("/", true) // Redirect to homepage on successful login
                .failureUrl("/auth?error") // Redirect back to combined auth page with error param on failure
                .permitAll() // Allow everyone to access the login form
            )
            // Configure logout
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout")) // URL to trigger logout
                .logoutSuccessUrl("/auth?logout") // Redirect to combined auth page after successful logout
                .invalidateHttpSession(true) // Invalidate HTTP session
                .deleteCookies("JSESSIONID") // Delete session cookie
                .permitAll() // Allow everyone to logout
            );

        return http.build();
    }
}
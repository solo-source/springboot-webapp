package com.stackit.controllers;

import com.stackit.dtos.RegisterDto;
import com.stackit.model.User;
import com.stackit.enums.UserRole;
import com.stackit.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // New consolidated method for showing login/register page
    @GetMapping("/auth")
    public String showAuthPage(Model model,
                               @RequestParam(name = "error", required = false) String error,
                               @RequestParam(name = "logout", required = false) String logout,
                               @RequestParam(name = "registered", required = false) String registered) {
        model.addAttribute("registerDto", new RegisterDto());
        model.addAttribute("loginError", error != null);
        model.addAttribute("logoutSuccess", logout != null);
        model.addAttribute("registrationSuccess", registered != null);
        return "auth/combined-auth"; // New template
    }

    // Handles user registration submission (path remains same, redirects change)
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("registerDto") RegisterDto registerDto,
                               BindingResult result,
                               Model model) {
        // Need to re-add parameters for combined-auth page if there's an error
        model.addAttribute("loginError", false); // Not a login error
        model.addAttribute("logoutSuccess", false); // Not a logout success
        model.addAttribute("registrationSuccess", false); // Only true on final redirect

        // Check for validation errors from @Valid annotations
        if (result.hasErrors()) {
            return "auth/combined-auth"; // Stay on the same page
        }

        // Check if username or email already exists (and is not deleted)
        if (userRepository.findByUsernameAndIsDeletedFalse(registerDto.getUsername()).isPresent()) {
            result.rejectValue("username", "error.registerDto", "Username already exists.");
            return "auth/combined-auth";
        }
        if (userRepository.findByEmailAndIsDeletedFalse(registerDto.getEmail()).isPresent()) {
            result.rejectValue("email", "error.registerDto", "Email already registered.");
            return "auth/combined-auth";
        }

        // Check if passwords match
        if (!registerDto.getPassword().equals(registerDto.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.registerDto", "Passwords do not match.");
            return "auth/combined-auth";
        }

        User newUser = new User();
        newUser.setUsername(registerDto.getUsername());
        newUser.setEmail(registerDto.getEmail());
        newUser.setPasswordHash(passwordEncoder.encode(registerDto.getPassword()));
        newUser.setRole(UserRole.USER);

        userRepository.save(newUser);

        return "redirect:/auth?registered"; // Redirect to combined auth page with success param
    }
}
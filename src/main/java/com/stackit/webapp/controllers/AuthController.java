package com.stackit.webapp.controllers;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.stackit.webapp.dtos.RegisterDto;
import com.stackit.webapp.enums.UserRole;
import com.stackit.webapp.model.User;
import com.stackit.webapp.repositories.UserRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/auth")
    public String showAuthPage(
        Model model,
        @RequestParam(name="loginError", required=false) Boolean loginError,
        @RequestParam(name="registered", required=false) Boolean registered
    ) {
        if (!model.containsAttribute("registerDto")) {
            model.addAttribute("registerDto", new RegisterDto());
        }
        model.addAttribute("loginError", loginError != null && loginError);
        model.addAttribute("registrationSuccess", registered != null && registered);
        return "auth/combined-auth";
    }

    /**
     * Handle POST /login
     */
    @PostMapping("/login")
    public String doLogin(
        @RequestParam String username,
        @RequestParam String password,
        HttpSession session,
        Model model
    ) {
        // Look up active user
        var opt = userRepository.findByUsernameAndIsDeletedFalse(username);
        if (opt.isEmpty() || !BCrypt.checkpw(password, opt.get().getPasswordHash())) {
            // failed -> redirect back with flag
            return "redirect:/auth?loginError";
        }

        // success -> store userId in session
        session.setAttribute("userId", opt.get().getId());
        return "redirect:/";  
    }

    /**
     * Handle POST /register
     */
    @PostMapping("/register")
    public String registerUser(
        @Valid @ModelAttribute("registerDto") RegisterDto registerDto,
        BindingResult result,
        Model model
    ) {
        // If validation errors, reshow form
        if (result.hasErrors()) {
            return showAuthPage(model, false, false);
        }

        // Check if username/email taken
        if (userRepository.findByUsernameAndIsDeletedFalse(registerDto.getUsername()).isPresent()) {
            result.rejectValue("username", "error.registerDto", "Username already exists.");
            return showAuthPage(model, false, false);
        }
        if (userRepository.findByEmailAndIsDeletedFalse(registerDto.getEmail()).isPresent()) {
            result.rejectValue("email", "error.registerDto", "Email already registered.");
            return showAuthPage(model, false, false);
        }

        // Check password confirmation
        if (!registerDto.getPassword().equals(registerDto.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.registerDto", "Passwords do not match.");
            return showAuthPage(model, false, false);
        }

        // Hash & save
        User u = new User();
        u.setUsername(registerDto.getUsername());
        u.setEmail(registerDto.getEmail());
        u.setPasswordHash(BCrypt.hashpw(registerDto.getPassword(), BCrypt.gensalt(12)));
        u.setRole(UserRole.USER);
        userRepository.save(u);

        // Redirect with a flag so we can show “Registration successful”
        return "redirect:/auth?registered=true";
    }


    @PostMapping("/logout")
    public String doLogout(HttpSession session) {
        session.invalidate();
        return "redirect:/auth?logout";
    }
}

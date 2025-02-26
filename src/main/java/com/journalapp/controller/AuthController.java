package com.journalapp.controller;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.journalapp.entity.LoginRequest;
import com.journalapp.entity.LoginResponse;
import com.journalapp.entity.RegisterRequest;
import com.journalapp.entity.User;
import com.jwt.helper.JwtUtil;
import com.journalapp.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        Optional<User> user = Optional.ofNullable(userRepository.findByUsername(loginRequest.getUsername()));
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        String token = jwtUtils.generateToken(user.get().getUsername());
        return ResponseEntity.ok(new LoginResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
        if (userRepository.findByUsername(registerRequest.getUsername()) != null) {
            return ResponseEntity.badRequest().body("Username already taken.");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRoles(Arrays.asList("USER"));

        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully.");
    }
}

//@RequestMapping("/public")
//public class PublicController {
//	
//	@Autowired
//	private UserService userService;
//
//	@GetMapping("/health-check")
//	public String healthCheck() {
//		return "ok";
//	}
//	
//	@PostMapping("/create-user")
//	public ResponseEntity<String> createUser(@RequestBody User user) {
//	    userService.saveNewUser(user);
//	    return ResponseEntity.ok("User created successfully!");
//	}
//
//}

package com.journalapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.journalapp.entity.User;
import com.journalapp.repository.UserRepository;
import com.journalapp.service.UserService;
import com.jwt.helper.JwtUtil;

import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
@RequestMapping("/user")
public class UserController {        
    
    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtUtil jwtUtil;

    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        System.out.println("Authentication: " + authentication);
        System.out.println("Authorities: " + authentication.getAuthorities());

        for (GrantedAuthority authority : authentication.getAuthorities()) {
            System.out.println("Authority: " + authority.getAuthority());
        }

        String username = authentication.getName();
        User userInDb = userService.findByUserName(username);

        if (userInDb == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        // ✅ Ensure the user can only update their own account
        if (!userInDb.getUsername().equals(username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not allowed to update this user");
        }

        // ✅ Update fields only if provided
        if (user.getUsername() != null && !user.getUsername().isEmpty()) {
            userInDb.setUsername(user.getUsername());
        }
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            userInDb.setPassword(passwordEncoder.encode(user.getPassword()));
        }
      
        userService.saveNewUser(userInDb);
      
     // 🔹 Generate a new JWT token since username changed
        String newToken = jwtUtil.generateToken(userInDb.getUsername()); 

        return ResponseEntity.ok().body("User updated successfully. Use new token: " + newToken);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteUserById() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        boolean deleted = userService.deleteUserAndEntries(username);

        if (deleted) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }

    
}

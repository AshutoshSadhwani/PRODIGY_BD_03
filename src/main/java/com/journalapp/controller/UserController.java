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

        userInDb.setUsername(user.getUsername());
        userInDb.setPassword(passwordEncoder.encode(user.getPassword()));

        userService.saveNewUser(userInDb);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
	public ResponseEntity<?> deleteUserById() {
    	
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        userRepository.deleteByUsername(authentication.getName());
    	
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}
    
}

package com.journalapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.journalapp.entity.User;
import com.journalapp.service.UserService;

@RestController
@RequestMapping("/admin")
public class AdminController {
	
	@Autowired
	private UserService userService;
	
	@GetMapping("/all-users")
	public ResponseEntity<?> getAllUsers() {
		List<User> all = userService.getAllEntry();
		if(all!=null && !all.isEmpty()) {
			return ResponseEntity.ok(all);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}
	
	@PostMapping("/create-admin-user")
	public ResponseEntity<String> createUser(@RequestBody User user) {
	    if (userService.findByUserName(user.getUsername()) != null) {
	        return ResponseEntity.badRequest().body("Username already exists.");
	    }

	    userService.saveAdmin(user); // Use the existing method
	    return ResponseEntity.status(HttpStatus.CREATED).body("Admin user created successfully.");
	}

}

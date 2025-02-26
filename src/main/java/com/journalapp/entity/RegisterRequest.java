package com.journalapp.entity;

import lombok.Data;

@Data
public class RegisterRequest {
	
    private String username;
    private String password;
    private String role;
    
}
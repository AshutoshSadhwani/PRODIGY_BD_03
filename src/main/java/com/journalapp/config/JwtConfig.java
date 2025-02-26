package com.journalapp.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.jwt.helper.JwtUtil;

@Configuration
public class JwtConfig {

    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil();
    }
}

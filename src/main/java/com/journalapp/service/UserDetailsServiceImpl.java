package com.journalapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.journalapp.entity.User;
import com.journalapp.repository.UserRepository;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user=userRepository.findByUsername(username);
		if(user!=null) {
			UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
			.username(user.getUsername())
			.password(user.getPassword())
			.roles(user.getRoles().stream() // Convert List<String> to String array
                    .map(role -> role.replace("ROLE_", "")) // Remove "ROLE_" if it exists
                    .toArray(String[]::new))
			.build();
			return userDetails;
		}
		throw new UsernameNotFoundException("User not found:"+username);
	}

	
}

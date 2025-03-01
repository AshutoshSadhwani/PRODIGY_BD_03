package com.journalapp.service;

import java.util.Arrays;
import java.util.List;



import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.journalapp.entity.User;
import com.journalapp.repository.JournalEntryRepository;
import com.journalapp.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Correctly inject PasswordEncoder

    public void saveNewUser(User user) {
    	user.setPassword(passwordEncoder.encode(user.getPassword())); // Encrypt password correctly
    	user.setRoles(Arrays.asList("USER"));  
    	
        userRepository.save(user);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }
    public void saveAdmin(User user) {
     	user.setPassword(passwordEncoder.encode(user.getPassword())); // Encrypt password correctly
    	user.setRoles(Arrays.asList("USER","ADMIN"));  
    	
        userRepository.save(user);
    	
    }

    public List<User> getAllEntry() {
        return userRepository.findAll();
    }

    public Optional<User> findById(ObjectId id) {
        return userRepository.findById(id);
    }

    
    public boolean deleteUserAndEntries(String username) {
        User user = userRepository.findByUsername(username);

        if (user != null) {
            // ✅ Delete all journal entries of the user
            journalEntryRepository.deleteAll(user.getJournalentries());

            // ✅ Now delete the user
            userRepository.delete(user);
            return true;
        }
        return false;
    }

    

    public User findByUserName(String username) {
        return userRepository.findByUsername(username);
    }

	



    
    
}

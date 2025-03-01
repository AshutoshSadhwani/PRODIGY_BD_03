package com.journalapp.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.journalapp.entity.JournalEntry;
import com.journalapp.entity.User;
import com.journalapp.repository.JournalEntryRepository;

@Component
public class JournalEntryService {

	@Autowired
	private JournalEntryRepository journalEntryRepository;
	@Autowired
	private UserService userService;
	
	@Transactional
	public void saveEntry(JournalEntry journalEntry, String username) {
	    try {
	        User user = userService.findByUserName(username);
	        
	        // 🔥 Set the owner of this journal entry
	        journalEntry.setUserId(user.getId().toString());

	        // 🔥 Set the timestamp
	        journalEntry.setDate(LocalDateTime.now());

	        // 🔥 Save the journal entry
	        JournalEntry saved = journalEntryRepository.save(journalEntry);

	        // 🔥 Ensure user's journal entry list is not null
	        if (user.getJournalentries() == null) {
	            user.setJournalentries(new ArrayList<>());
	        }

	        // 🔥 Add the saved journal entry to the user's list
	        user.getJournalentries().add(saved);
	        userService.saveUser(user);

	    } catch (Exception e) {
	        System.out.println(e);
	        throw new RuntimeException("An error occurred while saving the entry.", e);
	    }
	}

	public void saveEntry(JournalEntry journalEntry) {	
		journalEntryRepository.save(journalEntry);
	}
	
	public List<JournalEntry> getAllEntry() {
		return journalEntryRepository.findAll();
	}
	
	public Optional<JournalEntry> findById(ObjectId id) {
		return journalEntryRepository.findById(id);
	}
	
	@Transactional
	public boolean deleteById(ObjectId id, String username) {
		boolean removed =false;
		
		try {
			User user=userService.findByUserName(username);
			removed = user.getJournalentries().removeIf(x->x.getId().equals(id));
			if(removed) {
				userService.saveUser(user);
				journalEntryRepository.deleteById(id);			
			}			
		}
		catch (Exception e) {
			System.out.println(e);
			throw new  RuntimeException("An error occurred while deleteing the entry.",e);
		}
		return removed;
	}
	

	
}

package com.journalapp.controller;

import com.journalapp.entity.JournalEntry;

import com.journalapp.entity.User;
import com.journalapp.service.JournalEntryService;
import com.journalapp.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/journal")
public class JournalEntryController2 {		
	
	@Autowired
	private JournalEntryService journalEntryService;
	
	@Autowired 
	private UserService userService;

//	@GetMapping
//	public List<JournalEntry> getAll(){
//		return journalEntryService.getAllEntry();
//	}
	@GetMapping
	public ResponseEntity<?> getAllJournalEntryOfUser(){
		
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username=authentication.getName();
      
        
		User user = userService.findByUserName(username);
		
//		List<JournalEntry> allEntry = journalEntryService.getAllEntry();
		List<JournalEntry> allEntry = user.getJournalentries();
		if(allEntry!=null && !allEntry.isEmpty()) {
			return ResponseEntity.status(HttpStatus.OK).body(allEntry);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		
	}
	
	@PostMapping
	public ResponseEntity<JournalEntry> createEntry(@RequestBody JournalEntry myEntry) {
		try {
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username=authentication.getName();
		      
			
		journalEntryService.saveEntry(myEntry,username);
		return ResponseEntity.status(HttpStatus.CREATED).body(myEntry);
		}
		catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
			
		}
	}
	
	@GetMapping("/id/{myid}")
	public ResponseEntity<?> getEntryById(@PathVariable ObjectId myid) {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username=authentication.getName();
		User user = userService.findByUserName(username);   
		
		List<JournalEntry> collect=user.getJournalentries().
				stream().filter(x->x.getId().equals(myid)).
				collect(Collectors.toList());
		
		if(!collect.isEmpty()) {
			Optional<JournalEntry> journalEntry= journalEntryService.findById(myid);
			if(journalEntry.isPresent()) {
				return ResponseEntity.status(HttpStatus.OK).body(journalEntry);

			}
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

	}
	
	@DeleteMapping("/id/{myid}")
	public ResponseEntity<?> deleteEntryById(@PathVariable ObjectId myid) {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username=authentication.getName();
	
		boolean removed = journalEntryService.deleteById(myid,username);
		if(removed) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}
		else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		
	}
	
	@PutMapping("/id/{id}")
	public ResponseEntity<?> updateEntryById(@PathVariable ObjectId id,
			@RequestBody JournalEntry newEntry) {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username=authentication.getName();
		User user = userService.findByUserName(username);   
		
		List<JournalEntry> collect=user.getJournalentries().
				stream().filter(x->x.getId().equals(id)).
				collect(Collectors.toList());
		
		if(!collect.isEmpty()) {
			Optional<JournalEntry> journalEntry= journalEntryService.findById(id);
			if(journalEntry.isPresent()) {
				JournalEntry old=journalEntry.get();
				old.setTitle(newEntry.getTitle()!=null && !newEntry.getTitle().equals("")
						? newEntry.getTitle():old.getTitle());
				old.setContent(newEntry.getContent()!=null && !newEntry.getContent().equals("")
						? newEntry.getContent():old.getContent());
				
				journalEntryService.saveEntry(old);
				return ResponseEntity.status(HttpStatus.OK).body(old);

			}
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		
//		
//		if(old!=null) {
//			old.setTitle(newEntry.getTitle()!=null && !newEntry.getTitle().equals("")
//					? newEntry.getTitle():old.getTitle());
//			old.setContent(newEntry.getContent()!=null && !newEntry.getContent().equals("")
//					? newEntry.getContent():old.getContent());
//			
//			journalEntryService.saveEntry(old);
//			return ResponseEntity.status(HttpStatus.OK).body(old);
//		}
//		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
}
	
}

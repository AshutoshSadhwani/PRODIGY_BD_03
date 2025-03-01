package com.journalapp.controller;

import com.journalapp.entity.JournalEntry;
import com.journalapp.entity.User;
import com.journalapp.service.JournalEntryService;
import com.journalapp.service.UserService;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/journal")
public class JournalEntryController2 {        
    
    @Autowired
    private JournalEntryService journalEntryService;
    
    @Autowired 
    private UserService userService;

    // ✅ Get all journal entries of the logged-in user
    @GetMapping
    public ResponseEntity<?> getAllJournalEntryOfUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUserName(username);
        
        List<JournalEntry> allEntries = user.getJournalentries();
        if (allEntries != null && !allEntries.isEmpty()) {
            return ResponseEntity.ok(allEntries);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No journal entries found.");
    }

    // ✅ Create a new journal entry
    @PostMapping
    public ResponseEntity<?> createEntry(@RequestBody JournalEntry myEntry) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            journalEntryService.saveEntry(myEntry, username);
            return ResponseEntity.status(HttpStatus.CREATED).body(myEntry);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create journal entry.");
        }
    }

    // ✅ Get a specific journal entry by ID (only if user owns it)
    @GetMapping("/id/{myid}")
    public ResponseEntity<?> getEntryById(@PathVariable ObjectId myid) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUserName(username);

        Optional<JournalEntry> journalEntry = journalEntryService.findById(myid);

        if (journalEntry.isPresent() && journalEntry.get().getUserId().equals(user.getId().toString())) {
            return ResponseEntity.ok(journalEntry.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Journal entry not found or access denied.");
    }

    // ✅ Delete a journal entry by ID (only if user owns it)
    @DeleteMapping("/id/{myid}")
    public ResponseEntity<?> deleteEntryById(@PathVariable ObjectId myid) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUserName(username);

        Optional<JournalEntry> optionalEntry = journalEntryService.findById(myid);
        if (optionalEntry.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Journal entry not found.");
        }

        JournalEntry entry = optionalEntry.get();
        if (!entry.getUserId().equals(user.getId().toString())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not allowed to delete this entry.");
        }

        boolean removed = journalEntryService.deleteById(myid, username);
        if (removed) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete entry.");
    }

    // ✅ Update a journal entry by ID (only if user owns it)
    @PutMapping("/id/{id}")
    public ResponseEntity<?> updateEntryById(@PathVariable ObjectId id, @RequestBody JournalEntry newEntry) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUserName(username);

        Optional<JournalEntry> journalEntryOpt = journalEntryService.findById(id);
        if (journalEntryOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Journal entry not found.");
        }

        JournalEntry journalEntry = journalEntryOpt.get();
        if (!journalEntry.getUserId().equals(user.getId().toString())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not allowed to modify this entry!");
        }

        // ✅ Update title and content only if provided
        journalEntry.setTitle(newEntry.getTitle() != null && !newEntry.getTitle().isEmpty()
                ? newEntry.getTitle() : journalEntry.getTitle());
        journalEntry.setContent(newEntry.getContent() != null && !newEntry.getContent().isEmpty()
                ? newEntry.getContent() : journalEntry.getContent());

        journalEntryService.saveEntry(journalEntry, username);
        return ResponseEntity.ok(journalEntry);
    }
}

package com.journalapp.entity;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NonNull;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "users")
@Data
public class User {


	@Id
    private ObjectId id;  // ✅ Using ObjectId directly

    @Indexed(unique = true)
    @NonNull
    private String username;

    @NonNull
    private String password;

    @DBRef
    private List<JournalEntry> journalentries = new ArrayList<>();
    
    private List<String> roles;

    public User() {
    	
    }
}

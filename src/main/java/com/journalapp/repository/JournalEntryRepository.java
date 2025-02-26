package com.journalapp.repository;

import org.bson.types.ObjectId;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.journalapp.entity.JournalEntry;

public interface JournalEntryRepository extends MongoRepository<JournalEntry, ObjectId> {

}

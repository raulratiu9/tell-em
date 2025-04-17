package com.tellem.repository;

import com.tellem.model.Choice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChoiceRepository extends MongoRepository<Choice, String> {
}

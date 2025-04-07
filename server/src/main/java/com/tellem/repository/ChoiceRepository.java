package com.tellem.repository;

import com.tellem.model.Choice;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface ChoiceRepository extends ReactiveNeo4jRepository<Choice, UUID> {

    Flux<Choice> findAll();
}


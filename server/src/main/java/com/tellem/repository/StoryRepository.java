package com.tellem.repository;

import com.tellem.model.Story;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface StoryRepository extends ReactiveNeo4jRepository<Story, UUID> {
    Mono<Story> findByTitle(String title);
}


package com.tellem.repository;

import com.tellem.model.Story;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface StoryRepository extends ReactiveNeo4jRepository<Story, Long> {
    Mono<Story> findByTitle(String title);
}



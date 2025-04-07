package com.tellem.repository;

import com.tellem.model.Frame;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface FrameRepository extends ReactiveNeo4jRepository<Frame, UUID> {

    Flux<Frame> findAll();
}

package com.tellem.repository;

import com.tellem.model.Frame;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FrameRepository extends MongoRepository<Frame, Long> {
}

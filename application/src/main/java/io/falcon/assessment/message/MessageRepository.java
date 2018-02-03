package io.falcon.assessment.message;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;

public interface MessageRepository extends MongoRepository<Message, String> {

    List<Message> findByCreateTimeBefore(Instant createTime, Pageable pageable);
}

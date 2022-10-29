package com.bocanjis.quizzybase.util;

import com.bocanjis.quizzybase.documents.Question;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty("mongo.dropCollections")
@Log4j2
@RequiredArgsConstructor
public class MongoRunner implements ApplicationRunner {

    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        mongoTemplate.dropCollection(Question.class)
                .doOnNext(item -> log.info("Dropping collections"))
                .doOnSuccess(item -> log.info("Finished dropping collections..."))
                .subscribe();
    }
}

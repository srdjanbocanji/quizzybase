package com.bocanjis.quizzybase.util;

import com.bocanjis.quizzybase.documents.Question;
import com.bocanjis.quizzybase.dto.BatchInsertDto;
import com.bocanjis.quizzybase.service.QuestionService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Component;


@Component
@ConditionalOnProperty("mongo.dropCollections")
@Log4j2
@RequiredArgsConstructor
public class MongoRunner implements ApplicationRunner {

    private final QuestionService questionService;
    private final ReactiveMongoTemplate mongoTemplate;
    private final ObjectMapper objectMapper;

    @Override
    @SneakyThrows
    public void run(ApplicationArguments args) throws Exception {
        log.info("Dropping questions collection and re-populating data...");
        BatchInsertDto questions = objectMapper.readValue(new ClassPathResource("mass_questions.json").getFile(), new TypeReference<BatchInsertDto>() {});
        mongoTemplate.dropCollection(Question.class)
                .then(questionService.batchInsertQuestions(questions))
                .doOnSuccess(v -> log.info("Finished populating questions.."))
                .subscribe();
    }

}

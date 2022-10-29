package com.bocanjis.quizzybase.service;

import com.bocanjis.quizzybase.conversion.QuestionMapper;
import com.bocanjis.quizzybase.documents.Question;
import com.bocanjis.quizzybase.dto.BatchInsertDto;
import com.bocanjis.quizzybase.dto.QuestionDto;
import com.bocanjis.quizzybase.repository.QuestionRepository;
import com.bocanjis.quizzybase.util.QuestionCriteria;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
@Log4j2
public class QuestionService {

    private final QuestionMapper questionMapper;
    private final QuestionRepository questionRepository;
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    public Flux<QuestionDto> getQuestions(Integer page,
                                          Integer size,
                                          String search,
                                          List<String> categories,
                                          boolean searchDescription) {
        return reactiveMongoTemplate.find(getQuestionsQuery(page, size, search, categories, searchDescription), Question.class)
                .doOnNext(question -> log.debug("Found question {}...", question))
                .map(questionMapper);
    }

    public Mono<QuestionDto> findById(String id) {
        log.debug("Getting question by id {}...", id);
        return questionRepository.findById(id)
                .switchIfEmpty(Mono.error(IllegalArgumentException::new))
                .map(questionMapper);
    }

    public Mono<QuestionDto> create(QuestionDto questionDto) {
        log.debug("Creating new question... {}", questionDto);
        Question question = Optional.of(questionDto)
                .map(questionMapper::toQuestion)
                .orElseThrow(IllegalArgumentException::new);
        return questionRepository.insert(question).map(questionMapper);
    }

    public Mono<QuestionDto> update(String id, QuestionDto questionDto) {
        log.debug("Updating question with id {}, new values {}...", id, questionDto);
        return questionRepository.findById(id)
                .switchIfEmpty(Mono.error(IllegalArgumentException::new))
                .map(question -> questionMapper.toQuestion(questionDto, question))
                .flatMap(questionRepository::save)
                .map(questionMapper);
    }

    public Mono<Void> batchInsertQuestions(BatchInsertDto questions) {
        List<Question> items = questions.getItems()
                .stream().map(questionMapper::toQuestion)
                .filter(Objects::nonNull)
                .filter(question -> Objects.nonNull(question.getAnswer()) && Objects.nonNull(question.getQuestion()))
                .collect(Collectors.toList());
        return Mono.just(items)
                .flatMapMany(questionRepository::saveAll)
                .doOnNext(item -> log.info("Saved question {}...", item))
                .then();
    }

    private Query getQuestionsQuery(Integer page, Integer size, String search, List<String> categories, boolean searchDescription) {
        Query query = new Query();
        Optional.ofNullable(search)
                .map(value -> getSearchCriteria(value, searchDescription))
                .ifPresent(query::addCriteria);
        Optional.ofNullable(categories)
                .filter(values -> !categories.isEmpty())
                .map(QuestionCriteria::categoryContains)
                .ifPresent(query::addCriteria);

        return query.limit(size).skip(page * size);
    }

    private Criteria getSearchCriteria(String search, boolean searchDescription) {
        if(searchDescription) {
            return new Criteria()
                    .orOperator(QuestionCriteria.questionContains(search),
                            (QuestionCriteria.tagsContains(search)));
        }
        return QuestionCriteria.tagsContains(search);
    }
}

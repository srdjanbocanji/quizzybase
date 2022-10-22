package com.bocanjis.quizzybase.service;

import com.bocanjis.quizzybase.conversion.QuestionMapper;
import com.bocanjis.quizzybase.documents.Question;
import com.bocanjis.quizzybase.dto.QuestionDto;
import com.bocanjis.quizzybase.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;


@Component
@RequiredArgsConstructor
@Log4j2
public class QuestionService {

    private final QuestionMapper questionMapper;
    private final QuestionRepository questionRepository;

    public Flux<QuestionDto> getAllQuestions() {
        log.debug("Getting all questions...");
        return questionRepository.findAll()
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
}

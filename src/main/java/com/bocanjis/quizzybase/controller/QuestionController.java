package com.bocanjis.quizzybase.controller;

import com.bocanjis.quizzybase.dto.QuestionDto;
import com.bocanjis.quizzybase.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping
    public Mono<ResponseEntity<List<QuestionDto>>> getAllQuestions() {
        return questionService.getAllQuestions()
                .collectList()
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<QuestionDto>> getQuestionById(@PathVariable String id) {
        return questionService.findById(id).map(ResponseEntity::ok);
    }

    @PostMapping
    public Mono<ResponseEntity<QuestionDto>> createQuestion(@RequestBody QuestionDto questionDto) {
        return questionService.create(questionDto).map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<QuestionDto>> updateQuestion(@PathVariable String id, @RequestBody QuestionDto questionDto) {
        return questionService.update(id, questionDto).map(ResponseEntity::ok);
    }
}

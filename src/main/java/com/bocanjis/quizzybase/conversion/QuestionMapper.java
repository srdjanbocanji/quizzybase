package com.bocanjis.quizzybase.conversion;

import com.bocanjis.quizzybase.documents.Question;
import com.bocanjis.quizzybase.dto.QuestionDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class QuestionMapper implements Function<Question, QuestionDto> {

    private final ObjectMapper objectMapper;

    @Override
    @SneakyThrows
    public QuestionDto apply(Question question) {
        return objectMapper.readValue(objectMapper.writeValueAsString(question), QuestionDto.class);
    }

    @SneakyThrows
    public Question toQuestion(QuestionDto questionDto) {
        return objectMapper.readValue(objectMapper.writeValueAsString(questionDto), Question.class);
    }

    public Question toQuestion(QuestionDto questionDto, Question question) {
        return question.toBuilder()
                .question(questionDto.getQuestion())
                .answer(questionDto.getAnswer())
                .tags(questionDto.getTags())
                .categories(questionDto.getCategories())
                .description(questionDto.getDescription())
                .build();
    }
}

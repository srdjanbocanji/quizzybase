package com.bocanjis.quizzybase.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class QuestionDto {

    private String id;
    private String question;
    private List<String> categories;
    private List<String> tags;
    private String wikiLink;
    private String answer;
    private String description;
}

package com.bocanjis.quizzybase.documents;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "questions")
@Data
@Builder(toBuilder = true)
public class Question {

    private String id;
    private String question;
    @Indexed
    private List<String> categories;
    @Indexed
    private List<String> tags;
    private String answer;
    private String wikiLink;
    private String description;
}

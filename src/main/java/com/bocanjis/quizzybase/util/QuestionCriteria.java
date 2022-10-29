package com.bocanjis.quizzybase.util;

import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;

public class QuestionCriteria {

    public static Criteria questionContains(String value) {
        return Criteria.where("question").regex(value, "i");
    }

    public static Criteria tagsContains(String value) {
        return Criteria.where("tags").regex(value, "i");
    }

    public static Criteria categoryContains(List<String> categories) {
        String regex = String.join("|", categories);
        return Criteria.where("categories").regex(regex, "i");
    }
}

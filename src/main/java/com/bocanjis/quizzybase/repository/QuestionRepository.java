package com.bocanjis.quizzybase.repository;

import com.bocanjis.quizzybase.documents.Question;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface QuestionRepository extends ReactiveMongoRepository<Question, String> {
}

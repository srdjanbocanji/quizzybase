package com.bocanjis.quizzybase;

import com.bocanjis.quizzybase.config.BaseIntegrationTest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class QuestionControllerTests extends BaseIntegrationTest {

    @Test
    @SneakyThrows
    void testGetAllQuestions() {
        client.get()
                .uri("/questions")
                .exchange()
                .expectStatus().is2xxSuccessful();
    }
}

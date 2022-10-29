package com.bocanjis.quizzybase;

import com.bocanjis.quizzybase.config.BaseIntegrationTest;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.hasSize;


public class QuestionControllerTests extends BaseIntegrationTest {

    @Test
    @SneakyThrows
    void testGetQuestionsNoParameters() {
        client.get()
                .uri("/questions")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$").value(hasSize(10))
        ;
    }

    @Test
    void testGetQuestions_pagination() {
        client.get().uri("/questions?page=0&size=5").exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$").value(hasSize(5));
    }

    @Test
    void testGetQuestions_paginationNoMoreItems() {
        client.get().uri("/questions?page=2&size=4").exchange().expectStatus().is2xxSuccessful()
                .expectBody().jsonPath("$").value(hasSize(2))
        .jsonPath("$[0].answer").isEqualTo("Pagoda");
    }


    @Test
    void testGetQuestions_includeCategories() {
        client.get().uri("/questions?categories=Opsta kultura")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$").value(hasSize(6))
        .jsonPath("$[0].question").isEqualTo("Ko je komponovao himnu Boze pravde?")
        .jsonPath("$[1].answer").isEqualTo("MELANHOLIKE");
    }

    @Test
    void testGetQuestions_multipleCategories() {
        client.get().uri("/questions?categories=Opsta kUltura,Istorija,sport")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$").value(hasSize(8))
                .jsonPath("$[0].question").isEqualTo("Koji motociklista je proslavio broj 46?")
                .jsonPath("$[2].answer").isEqualTo("MELANHOLIKE");
    }

    @Test
    void testGetQuestions_searchCriteria() {
        client.get().uri("/questions?search=reziser")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$").value(hasSize(2))
                .jsonPath("$[0].question").isEqualTo("KO JE REŽIRAO ČUVENI FILM „VRTOGLAVICA“ S DžEJMSOM STJUARTOM I KIM NOVAK U GLAVNIM ULOGAMA?")
                .jsonPath("$[1].answer").isEqualTo("Oblakodera");
    }

    @Test
    void testGetQuestions_searchCriteriaWithCategories() {
        client.get().uri("/questions?search=reziser&categories=Film&page=0&size=10")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$").value(hasSize(1))
                .jsonPath("$[0].question").isEqualTo("KO JE REŽIRAO ČUVENI FILM „VRTOGLAVICA“ S DžEJMSOM STJUARTOM I KIM NOVAK U GLAVNIM ULOGAMA?");
    }



    @Test
    void testCreateQuestion() {
        String body = "{\n" +
                "    \"question\": \"Koji motociklista je proslavio broj 46?\",\n" +
                "    \"categories\": [\"Sport\"],\n" +
                "    \"tags\": [\"Moto GP\", \"Valentino Rosi\"],\n" +
                "    \"answer\": \"Valentino Rosi\",\n" +
                "    \"description\": \"\",\n" +
                "    \"wikiLink\": \"https://sr.wikipedia.org/wiki/%D0%92%D0%B0%D0%BB%D0%B5%D0%BD%D1%82%D0%B8%D0%BD%D0%BE_%D0%A0%D0%BE%D1%81%D0%B8\"\n" +
                "  }";

        client.post()
                .uri("/questions")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.question").isEqualTo("Koji motociklista je proslavio broj 46?")
                .jsonPath("$.answer").isEqualTo("Valentino Rosi")
                .jsonPath("$.categories[0]").isEqualTo("Sport")
                .jsonPath("$.tags[0]").isEqualTo("Moto GP")
                .jsonPath("$.wikiLink").isNotEmpty()
                .jsonPath("$.id").isNotEmpty();
    }

    @Test
    @SneakyThrows
    void testBatchInsert() {
        JsonNode body = objectMapper.readValue(new ClassPathResource("mass_questions.json").getFile(), JsonNode.class);
        client.post()
                .uri("/questions/batch-insert")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().is2xxSuccessful();
    }


}

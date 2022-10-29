package com.bocanjis.quizzybase.config;

import com.bocanjis.quizzybase.documents.Question;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

@Testcontainers
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BaseIntegrationTest {

    @Container
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:5"));
    protected WebTestClient client;
    @Autowired
    protected ApplicationContext context;
    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;
    @Autowired
    protected ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        client = WebTestClient.bindToApplicationContext(context).configureClient()
                .exchangeStrategies(ExchangeStrategies.builder().codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(16 * 1024 * 1024)).build())
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024 * 16))
                .build();
        fillQuestionsData();
    }

    @DynamicPropertySource
    static void mongoDbProperties(DynamicPropertyRegistry registry) {
        registry.add("mongo.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("mongo.database", () -> "quizzybase");
    }

    @SneakyThrows
    void fillQuestionsData() {
      List<Question> questions = objectMapper.readValue(new ClassPathResource("questions.json").getFile(), new TypeReference<List<Question>>() {});
      reactiveMongoTemplate
              .remove(new Query(), Question.class)
              .then(reactiveMongoTemplate.insertAll(questions).collectList()).block();
    }
}

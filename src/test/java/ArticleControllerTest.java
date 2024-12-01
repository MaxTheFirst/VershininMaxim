import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.ObjectMapper;
import controllers.article.ArticleController;
import controllers.article.responses.ArticleCreateResponse;
import controllers.article.responses.ArticleGetResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repositories.article.ArticleRepository;
import repositories.article.InMemoryArticleRepository;
import repositories.comment.CommentRepository;
import repositories.comment.InMemoryCommentRepository;
import services.ArticleService;
import spark.Service;
import spark.template.freemarker.FreeMarkerEngine;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArticleControllerTest {
  private static final int CREATED_STATUS_CODE = 201;
  private static final int FIRST_ID = 1;

  private Service service;
  private ObjectMapper objectMapper;

  private int port;

  @BeforeEach
  void beforeEach() {
    service = Service.ignite();
    port = 4567;
    initApp();
  }

  private void initApp() {
    Service service = Service.ignite();
    objectMapper = new ObjectMapper();
    ArticleRepository articles = new InMemoryArticleRepository();
    CommentRepository comments = new InMemoryCommentRepository();

    Application application =
        new Application(
            List.of(
                new ArticleController(
                    service,
                    new ArticleService(articles, comments),
                    objectMapper
                )
            )
        );

    application.start();
    service.awaitInitialization();
  }

  @AfterEach
  void afterEach() {
    service.stop();
    service.awaitStop();
  }

  @Test
  void createArticleTest() throws IOException, InterruptedException {
    HttpResponse<String> response = HttpClient.newHttpClient()
        .send(
            HttpRequest.newBuilder()
                .POST(
                    HttpRequest.BodyPublishers.ofString(
                        """
                                { "name": "first", "tags": ["t1", "t2"]}
                              """
                    )
                )
                .uri(URI.create("http://localhost:%d/api/articles".formatted(port)))
                .build(),
            HttpResponse.BodyHandlers.ofString(UTF_8)
        );

    assertEquals(CREATED_STATUS_CODE, response.statusCode());
    ArticleCreateResponse articleCreateResponse =
        objectMapper.readValue(response.body(), ArticleCreateResponse.class);
    assertEquals(FIRST_ID, articleCreateResponse.articleId());
  }

  @Test
  void getArticleTest() throws IOException, InterruptedException {
    HttpResponse<String> response = HttpClient.newHttpClient()
        .send(
            HttpRequest.newBuilder()
                .GET()
                .uri(
                    URI.create(
                        "http://localhost:%d/api/articles/%d".formatted(port, FIRST_ID)
                    )
                )
                .build(),
            HttpResponse.BodyHandlers.ofString(UTF_8)
        );

    ArticleGetResponse articleGetResponse =
        objectMapper.readValue(response.body(), ArticleGetResponse.class);

    assertEquals(CREATED_STATUS_CODE, response.statusCode());

    assertEquals(FIRST_ID, articleGetResponse.articleId());
    assertEquals("first", articleGetResponse.name());
    assertEquals("t1", articleGetResponse.tags().toArray()[0]);
  }
}
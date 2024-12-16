import com.fasterxml.jackson.databind.ObjectMapper;
import controllers.article.ArticleController;
import controllers.article.responses.*;
import controllers.comment.CommentController;
import controllers.comment.responses.*;
import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import repositories.article.ArticleRepository;
import repositories.article.InMemoryArticleRepository;
import repositories.comment.CommentRepository;
import repositories.comment.InMemoryCommentRepository;
import services.ArticleService;
import services.CommentService;
import spark.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
class E2ETest {
  private static final int CREATED_STATUS_CODE = 201;
  private static final long ARTICLE_ID = 1;
  private static final long COMMENT_ID = 1;

  private Service service;
  private ObjectMapper objectMapper;
  @Container
  public static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:13");

  static {
    POSTGRES.start();
  }

  private static Jdbi jdbi;

  private int port;

  @BeforeAll
  static void beforeAll() {
    String postgresJdbcUrl = POSTGRES.getJdbcUrl();
    Flyway flyway =
        Flyway.configure()
            .outOfOrder(true)
            .locations("classpath:db/migrations")
            .dataSource(postgresJdbcUrl, POSTGRES.getUsername(), POSTGRES.getPassword())
            .load();
    flyway.migrate();
    jdbi = Jdbi.create(postgresJdbcUrl, POSTGRES.getUsername(), POSTGRES.getPassword());
  }

  @BeforeEach
  void beforeEach() {
    deleteAll();
    service = Service.ignite();
    initApp();
    port = service.port();
  }

  void deleteAll() {
    jdbi.inTransaction((Handle ownHandle) -> {
      ownHandle.createUpdate("DELETE FROM articles").execute();
      ownHandle.createUpdate("DELETE FROM comments").execute();
      return null;
    });
  }

  private void initApp() {
    service = Service.ignite();
    objectMapper = new ObjectMapper();
    ArticleRepository articles = new InMemoryArticleRepository(jdbi);
    CommentRepository comments = new InMemoryCommentRepository(jdbi);

    Application application = new Application(
        List.of(
            new ArticleController(service, new ArticleService(articles), objectMapper),
            new CommentController(service, new CommentService(articles, comments), objectMapper)
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
  void e2eTest() throws IOException, InterruptedException {
    HttpResponse<String> response = HttpClient.newHttpClient()
        .send(
            HttpRequest.newBuilder()
                .POST(
                    HttpRequest.BodyPublishers.ofString(
                        """
                        { "name": "First Article", "tags": ["tag1", "tag2"] }
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
    assertEquals(ARTICLE_ID, articleCreateResponse.articleId());

    response = HttpClient.newHttpClient()
        .send(
            HttpRequest.newBuilder()
                .POST(
                    HttpRequest.BodyPublishers.ofString(
                        """
                        { "articleId": %d, "text": "This is a comment" }
                        """.formatted(ARTICLE_ID))
                )
                .uri(URI.create("http://localhost:%d/api/comments".formatted(port)))
                .build(),
            HttpResponse.BodyHandlers.ofString(UTF_8)
        );
    assertEquals(CREATED_STATUS_CODE, response.statusCode());
    CommentCreateResponse commentCreateResponse =
        objectMapper.readValue(response.body(), CommentCreateResponse.class);
    assertEquals(COMMENT_ID, commentCreateResponse.id());

    response = HttpClient.newHttpClient()
        .send(
            HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:%d/api/articles/%d".formatted(port, ARTICLE_ID)))
                .build(),
            HttpResponse.BodyHandlers.ofString(UTF_8)
        );
    ArticleGetResponse articleGetResponse =
        objectMapper.readValue(response.body(), ArticleGetResponse.class);
    assertEquals(CREATED_STATUS_CODE, response.statusCode());
    assertEquals(ARTICLE_ID, articleGetResponse.articleId());
    assertEquals("First Article", articleGetResponse.name());
    assertEquals("tag1", articleGetResponse.tags().toArray()[0]);

    response = HttpClient.newHttpClient()
        .send(
            HttpRequest.newBuilder()
                .PUT(
                    HttpRequest.BodyPublishers.ofString(
                        """
                        { "name": "Updated Article", "tags": ["tag3"] }
                        """
                    )
                )
                .uri(URI.create("http://localhost:%d/api/articles/%d".formatted(port, ARTICLE_ID)))
                .build(),
            HttpResponse.BodyHandlers.ofString(UTF_8)
        );
    assertEquals(CREATED_STATUS_CODE, response.statusCode());
    ArticleUpdateResponse articleUpdateResponse =
        objectMapper.readValue(response.body(), ArticleUpdateResponse.class);
    assertEquals(ARTICLE_ID, articleUpdateResponse.articleId());

    response = HttpClient.newHttpClient()
        .send(
            HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:%d/api/articles/%d".formatted(port, ARTICLE_ID)))
                .build(),
            HttpResponse.BodyHandlers.ofString(UTF_8)
        );
    articleGetResponse =
        objectMapper.readValue(response.body(), ArticleGetResponse.class);
    assertEquals(CREATED_STATUS_CODE, response.statusCode());
    assertEquals("Updated Article", articleGetResponse.name());
    assertEquals("tag3", articleGetResponse.tags().toArray()[0]);

    response = HttpClient.newHttpClient()
        .send(
            HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create("http://localhost:%d/api/comments/%d".formatted(port, COMMENT_ID)))
                .build(),
            HttpResponse.BodyHandlers.ofString(UTF_8)
        );
    CommentDeleteResponse commentDeleteResponse =
        objectMapper.readValue(response.body(), CommentDeleteResponse.class);
    assertEquals(COMMENT_ID, commentDeleteResponse.id());

    response = HttpClient.newHttpClient()
        .send(
            HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:%d/api/articles/%d".formatted(port, ARTICLE_ID)))
                .build(),
            HttpResponse.BodyHandlers.ofString(UTF_8)
        );
    articleGetResponse =
        objectMapper.readValue(response.body(), ArticleGetResponse.class);
    assertEquals(CREATED_STATUS_CODE, response.statusCode());
    assertEquals(ARTICLE_ID, articleGetResponse.articleId());
    assertEquals("Updated Article", articleGetResponse.name());
    assertEquals("tag3", articleGetResponse.tags().toArray()[0]);

    response = HttpClient.newHttpClient()
        .send(
            HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create("http://localhost:%d/api/articles/%d".formatted(port, ARTICLE_ID)))
                .build(),
            HttpResponse.BodyHandlers.ofString(UTF_8)
        );
    ArticleDeleteResponse articleDeleteResponse =
        objectMapper.readValue(response.body(), ArticleDeleteResponse.class);
    assertEquals(ARTICLE_ID, articleDeleteResponse.articleId());
  }
}

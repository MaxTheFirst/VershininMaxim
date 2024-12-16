import com.fasterxml.jackson.databind.ObjectMapper;
import controllers.article.ArticleController;
import controllers.comment.CommentController;
import controllers.comment.responses.*;
import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import repositories.article.ArticleRepository;
import repositories.article.InMemoryArticleRepository;
import repositories.comment.CommentRepository;
import repositories.comment.InMemoryCommentRepository;
import services.CommentService;
import services.ArticleService;
import spark.Service;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CommentControllerTest {
  private static final int CREATED_STATUS_CODE = 201;
  private static final long FIRST_COMMENT_ID = 1;
  private static final long ARTICLE_ID = 1;

  private Service service;
  private ObjectMapper objectMapper;
  private int port;

  @Container
  public static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:13");

  static {
    POSTGRES.start();
  }

  private static Jdbi jdbi;

  @BeforeEach
  void beforeEach() throws IOException, InterruptedException {
    service = Service.ignite();
    initApp();
    port = service.port();
    initDB();
    createArticle();
  }

  private void initApp() {
    service = Service.ignite();
    objectMapper = new ObjectMapper();
    ArticleRepository articles = new InMemoryArticleRepository(jdbi);
    CommentRepository comments = new InMemoryCommentRepository(jdbi);

    Application application =
        new Application(
            List.of(
                new ArticleController(service, new ArticleService(articles), objectMapper),
                new CommentController(service, new CommentService(articles, comments), objectMapper)
            )
        );

    application.start();
    service.awaitInitialization();
  }

  private void initDB() {
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

  @AfterEach
  void afterEach() {
    service.stop();
    service.awaitStop();
  }

  private void createArticle() throws IOException, InterruptedException {
    HttpResponse<String> response = HttpClient.newHttpClient()
        .send(
            HttpRequest.newBuilder()
                .POST(
                    HttpRequest.BodyPublishers.ofString(
                        """
                            { "name": "first article", "tags": ["tag1", "tag2"] }
                        """
                    )
                )
                .uri(URI.create("http://localhost:%d/api/articles".formatted(port)))
                .build(),
            HttpResponse.BodyHandlers.ofString(UTF_8)
        );
    assertEquals(CREATED_STATUS_CODE, response.statusCode());
  }

  @Test
  void createCommentTest() throws IOException, InterruptedException {
    HttpResponse<String> response = HttpClient.newHttpClient()
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
    assertEquals(FIRST_COMMENT_ID, commentCreateResponse.id());
  }

  @Test
  void getCommentTest() throws IOException, InterruptedException {
    createCommentTest();
    HttpResponse<String> response = HttpClient.newHttpClient()
        .send(
            HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:%d/api/comments/%d".formatted(port, FIRST_COMMENT_ID)))
                .build(),
            HttpResponse.BodyHandlers.ofString(UTF_8)
        );

    CommentGetResponse commentGetResponse =
        objectMapper.readValue(response.body(), CommentGetResponse.class);

    assertEquals(CREATED_STATUS_CODE, response.statusCode());
    assertEquals(FIRST_COMMENT_ID, commentGetResponse.id());
    assertEquals("This is a comment", commentGetResponse.text());
  }

  @Test
  void updateCommentTest() throws IOException, InterruptedException {
    createCommentTest();
    HttpResponse<String> response = HttpClient.newHttpClient()
        .send(
            HttpRequest.newBuilder()
                .PUT(
                    HttpRequest.BodyPublishers.ofString(
                        """
                            { "text": "Updated comment text" }
                        """
                    )
                )
                .uri(URI.create("http://localhost:%d/api/comments/%d".formatted(port, FIRST_COMMENT_ID)))
                .build(),
            HttpResponse.BodyHandlers.ofString(UTF_8)
        );

    assertEquals(CREATED_STATUS_CODE, response.statusCode());
    CommentUpdateResponse commentUpdateResponse =
        objectMapper.readValue(response.body(), CommentUpdateResponse.class);
    assertEquals(FIRST_COMMENT_ID, commentUpdateResponse.id());
  }

  @Test
  void deleteCommentTest() throws IOException, InterruptedException {
    createCommentTest();
    HttpResponse<String> response = HttpClient.newHttpClient()
        .send(
            HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create("http://localhost:%d/api/comments/%d".formatted(port, FIRST_COMMENT_ID)))
                .build(),
            HttpResponse.BodyHandlers.ofString(UTF_8)
        );

    CommentDeleteResponse commentDeleteResponse =
        objectMapper.readValue(response.body(), CommentDeleteResponse.class);
    assertEquals(FIRST_COMMENT_ID, commentDeleteResponse.id());
  }
}

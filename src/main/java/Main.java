
import com.fasterxml.jackson.databind.ObjectMapper;
import controllers.article.ArticleController;
import controllers.article.ArticleFreemarkerController;
import controllers.comment.CommentController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repositories.article.ArticleRepository;
import repositories.article.InMemoryArticleRepository;
import repositories.comment.CommentRepository;
import repositories.comment.InMemoryCommentRepository;
import services.ArticleService;
import services.CommentService;
import spark.Service;
import spark.template.freemarker.FreeMarkerEngine;

import java.util.List;

public class Main {
  private static final Logger LOG = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) {
    Service service = Service.ignite();
    ObjectMapper objectMapper = new ObjectMapper();
    ArticleRepository articles = new InMemoryArticleRepository();
    CommentRepository comments = new InMemoryCommentRepository();
    FreeMarkerEngine freeMarkerEngine = TemplateFactory.freeMarkerEngine();

    Application application =
        new Application(
            List.of(
                new ArticleController(
                    service,
                    new ArticleService(articles, comments),
                    objectMapper
                ),
                new CommentController(
                    service,
                    new CommentService(articles, comments),
                    objectMapper
                ),
                new ArticleFreemarkerController(
                    service,
                    new ArticleService(articles, comments),
                    freeMarkerEngine
                )
            )
        );
    application.start();
  }
}
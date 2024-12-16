package controllers.article;

import com.fasterxml.jackson.databind.ObjectMapper;
import controllers.Controller;
import controllers.article.requests.ArticleCreateRequest;
import controllers.article.requests.ArticleUpdateRequest;
import controllers.article.responses.*;
import exceptions.ArticleCreateException;
import exceptions.ArticleDeleteException;
import exceptions.ArticleNotFoundException;
import exceptions.ArticleUpdateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.ArticleService;
import spark.Request;
import spark.Response;
import spark.Service;
import types.article.Article;


public class ArticleController implements Controller {
  private static final Logger LOG = LoggerFactory.getLogger(ArticleController.class);
  private final Service service;
  private final ArticleService articleService;
  private final ObjectMapper objectMapper;

  public ArticleController(Service service, ArticleService articleService, ObjectMapper objectMapper) {
    this.service = service;
    this.articleService = articleService;
    this.objectMapper = objectMapper;
  }

  @Override
  public void initializeEndpoints() {
    createArticle();
    getArticle();
    updateArticle();
    deleteArticle();
  }

  private void createArticle() {
    service.post("/api/articles", (Request request, Response response) -> {
      response.type("application/json");
      String body = request.body();
      ArticleCreateRequest articleCreateRequest = objectMapper.readValue(body, ArticleCreateRequest.class);
      try {
        long articleId = articleService.createArticle(articleCreateRequest.name(), articleCreateRequest.tags());
        response.status(201);
        LOG.debug("Article is created");
        return objectMapper.writeValueAsString(new ArticleCreateResponse(articleId));
      } catch (ArticleCreateException e) {
        LOG.warn(e.getMessage(), e);
        response.status(400);
        return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
      }
    });
  }

  private void getArticle() {
    service.get("api/articles/:articleId", (Request request, Response response) -> {
      response.type("aplication/json");
      long articleId = Long.parseLong(request.params("articleId"));
      try {
        Article article = articleService.findArticleById(articleId);
        response.status(201);
        LOG.debug("Article is got");
        return objectMapper.writeValueAsString(new ArticleGetResponse(articleId, article.getName(), article.getTags()));
      } catch (ArticleNotFoundException e) {
        LOG.warn(e.getMessage(), e);
        response.status(400);
        return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
      }
    });
  }

  private void updateArticle() {
    service.put("/api/articles/:articleId", (Request request, Response response) -> {
      response.body("application/json");
      long articleId = Long.parseLong(request.params("articleId"));
      ArticleUpdateRequest articleUpdateRequest = objectMapper.readValue(request.body(), ArticleUpdateRequest.class);
      try {
        articleService.articleUpdate(articleId, articleUpdateRequest.name(), articleUpdateRequest.tags());
        response.status(201);
        LOG.debug("Article is updated");
        return objectMapper.writeValueAsString(new ArticleUpdateResponse(articleId));
      } catch (ArticleUpdateException e) {
        LOG.warn(e.getMessage(), e);
        response.status(400);
        return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
      }
    });
  }


  private void deleteArticle() {
    service.delete("/api/articles/:articleId", (Request request, Response response) -> {
      response.body("application/json");
      long articleId = Long.parseLong(request.params("articleId"));
      try {
        articleService.deleteArticle(articleId);
        response.status(201);
        LOG.debug("Article is deleted");
        return objectMapper.writeValueAsString(new ArticleDeleteResponse(articleId));
      } catch (ArticleDeleteException e) {
        LOG.warn("cannot delete article", e);
        response.status(400);
        return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
      }
    });
  }
}

package controllers.comment;

import controllers.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.ArticleService;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Service;
import spark.template.freemarker.FreeMarkerEngine;
import types.article.Article;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArticleFreemarkerController implements Controller {
  private static final Logger LOG = LoggerFactory.getLogger(ArticleFreemarkerController.class);
  private final Service service;
  private final ArticleService articleService;
  private final FreeMarkerEngine freeMarkerEngine;

  public ArticleFreemarkerController(Service service, ArticleService articleService, FreeMarkerEngine freeMarkerEngine) {
    this.service = service;
    this.articleService = articleService;
    this.freeMarkerEngine = freeMarkerEngine;
  }

  @Override
  public void initializeEndpoints() {
    getAllArticles();
  }

  private void getAllArticles() {
    service.get("/", (Request request, Response response) -> {
      response.type("text/html; charset=utf-8");
      List<Article> articles = articleService.findAll();
      List<Map<String, String>> articleMapList = articles.stream()
          .map(article -> Map.of("name", article.getName(), "tags", article.getTags().toString(), "comments", article.getCommentsString())).toList();
      Map<String, Object> model = new HashMap<>();
      model.put("articles", articleMapList);
      LOG.debug("Articles shown");
      return freeMarkerEngine.render(new ModelAndView(model, "index.ftl"));
    });
  }
}

package repositories.article;

import types.article.Article;

import java.util.List;

public interface ArticleRepository {
  long generateId();

  List<Article> findAll();

  Article findById(long articleId);

  void create(Article article);

  void update(Article article);

  void delete(long articleId);
}

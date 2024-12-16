package repositories.article;

import types.article.Article;

import java.util.List;
import java.util.Optional;

public interface ArticleRepository {
  long generateId();

  List<Article> findAll();

  Optional<Article> findById(long articleId);

  void create(Article article);

  void update(Article article);

  void delete(long articleId);
}

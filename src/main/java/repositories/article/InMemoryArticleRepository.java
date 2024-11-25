package repositories.article;

import exceptions.ArticleIdDuplicatedException;
import exceptions.ArticleNotFoundException;
import types.article.Article;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryArticleRepository implements ArticleRepository{
  private final AtomicLong nextId = new AtomicLong(0);
  private final Map<Long, Article> articles = new ConcurrentHashMap<>();

  @Override
  public long generateId() {
    return nextId.incrementAndGet();
  }

  @Override
  public List<Article> findAll() {
    return new ArrayList<>(articles.values());
  }

  @Override
  public Article findById(long articleId) {
    Article article = articles.get(articleId);
    if (article == null) {
      throw new ArticleNotFoundException("Cannot find article by id=" + articleId);
    }
    return article;
  }

  @Override
  public synchronized void create(Article article) {
    if (articles.get(article.getId()) != null) {
      throw new ArticleIdDuplicatedException("Article with the given id already exists: " + article.getId());
    }
    articles.put(article.getId(), article);
  }

  @Override
  public synchronized void update(Article article) {
    if (articles.get(article.getId()) == null) {
      throw new ArticleNotFoundException("Cannot find article by id=" + article.getId());
    }
    articles.put(article.getId(), article);
  }

  @Override
  public void delete(long articleId) {
    if (articles.remove(articleId) == null) {
      throw new ArticleNotFoundException("Cannot find article by id=" + articleId);
    }
  }
}

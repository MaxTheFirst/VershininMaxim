package services;

import exceptions.ArticleDeleteException;
import exceptions.ArticleNotFoundException;
import exceptions.ArticleUpdateException;
import repositories.article.ArticleRepository;
import repositories.comment.CommentRepository;
import types.article.Article;
import types.article.ArticleId;
import types.comment.Comment;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ArticleService {
  private final ArticleRepository articleRepository;

  public ArticleService(ArticleRepository articleRepository) {
    this.articleRepository = articleRepository;
  }

  public List<Article> findAll() {
    return articleRepository.findAll();
  }

  public Article findArticleById(long id) {
    Optional<Article> article = articleRepository.findById(id);
    if (article.isEmpty()) {
      throw new ArticleNotFoundException("Cannot find article with id=" + id);
    }
    return article.get();
  }

  public long createArticle(String name, Set<String> tags) {
    Article article = new Article(new ArticleId(articleRepository.generateId()), name, tags);
    articleRepository.create(article);
    return article.getId();
  }

  public void articleUpdate(long articleId, String name, Set<String> tags) {
    Optional<Article> article = articleRepository.findById(articleId);
    if (article.isEmpty()) {
      throw new ArticleUpdateException("Cannot find article with id=" + articleId);
    }
    Article newArticle = article.get().withName(name).withTags(tags);
    articleRepository.update(newArticle);
  }

  public void deleteArticle(long articleId) {
    Optional<Article> article = articleRepository.findById(articleId);
    if (article.isEmpty()) {
      throw new ArticleDeleteException("Cannot delete book with id=" + articleId);
    }

    articleRepository.delete(articleId);
  }

}
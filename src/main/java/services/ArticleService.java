package services;

import exceptions.*;
import repositories.article.ArticleRepository;
import repositories.comment.CommentRepository;
import types.article.Article;
import types.article.ArticleId;
import types.comment.Comment;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ArticleService {
  private final ArticleRepository articleRepository;
  private final CommentRepository commentRepository;

  public ArticleService(ArticleRepository articleRepository, CommentRepository commentRepository) {
    this.articleRepository = articleRepository;
    this.commentRepository = commentRepository;
  }

  public List<Article> findAll() {
    return articleRepository.findAll();
  }

  public Article findArticleById(long id) {
    try {
      return articleRepository.findById(id);
    } catch (ArticleNotFoundException e) {
      throw new ArticleNotFoundException("Cannot find article with id=" + id);
    }
  }

  public long createArticle(String name, Set<String> tags) {
    long articleId = articleRepository.generateId();
    Article article = new Article(new ArticleId(articleId), name, tags);
    try {
      articleRepository.create(article);
      return article.getId();
    } catch (ArticleIdDuplicatedException e) {
      throw new ArticleCreateException("Cannot create article");
    }
  }

  public void articleUpdate(long articleId, String name, Set<String> tags) {
    Article article;
    try {
      article = articleRepository.findById(articleId);
    } catch (ArticleNotFoundException e) {
      throw new ArticleUpdateException("Cannot find article with id=" + articleId);
    }
    article = article.newArticle(name).newArticle(tags);
    articleRepository.update(article);
  }

  public void deleteArticle(long articleId) {
    Article article;
    try {
      article = articleRepository.findById(articleId);
    } catch (ArticleNotFoundException e) {
      throw new ArticleDeleteException("Cannot delete book with id=" + articleId);
    }

    for (Comment comment : article.getComments()) {
      commentRepository.delete(comment.getId());
    }
    articleRepository.delete(articleId);
  }

}
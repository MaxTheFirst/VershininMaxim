package types.comment;

import types.article.ArticleId;

public class Comment {
  private final CommentId id;
  private final ArticleId articleId;
  private String text;


  public Comment(CommentId id, ArticleId articleId, String text) {
    this.id = id;
    this.articleId = articleId;
    this.text = text;
  }

  public Long getId() {
    return id.getValue();
  }

  public ArticleId getArticleId() {
    return articleId;
  }

  public String getText() {
    return text;
  }

  public Comment newComment(String text) {
    return new Comment(id, articleId, text);
  }
}

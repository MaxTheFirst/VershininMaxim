package types.article;

import types.comment.Comment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Article {
  private final ArticleId id;
  private final String name;
  private final Set<String> tags;
  private List<Comment> comments;
  private boolean trending;

  public Article(ArticleId id, String name, Set<String> tags) {
    this.id = id;
    this.name = name;
    this.tags = tags;
  }

  public Long getId() {
    return id.getValue();
  }

  public String getName() {
    return name;
  }

  public Set<String> getTags() {
    return tags;
  }

  public void setComments(List<Comment> comments) {
    this.comments = comments;
  }

  public List<Comment> getComments() {
    return comments;
  }

  public void addComment(Comment comment) {
    if (comments == null) {
      comments = new ArrayList<>();
    }
    comments.add(comment);
  }

  public Article withName(String name) {
    if (name == null) {
      return this;
    }
    return new Article(id, name, tags);
  }

  public Article withTags(Set<String> tags) {
    if (name == null) {
      return this;
    }
    return new Article(id, name, tags);
  }

  public String getCommentsString() {
    return comments.toString();
  }

  public boolean isTrending() {
    return trending;
  }

  public Article setTrending(boolean trending) {
    this.trending = trending;
    return this;
  }
}

package controllers.article.requests;

import java.util.Set;

public record ArticleUpdateRequest(long articleId, String name, Set<String> tags) {
}

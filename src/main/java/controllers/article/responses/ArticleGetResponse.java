package controllers.article.responses;

import java.util.Set;

public record ArticleGetResponse(long articleId, String name, Set<String> tags) {
}

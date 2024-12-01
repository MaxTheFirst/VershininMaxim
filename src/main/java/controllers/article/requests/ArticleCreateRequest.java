package controllers.article.requests;

import java.util.Set;

public record ArticleCreateRequest(String name, Set<String> tags) {
}

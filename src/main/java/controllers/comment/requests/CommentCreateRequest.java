package controllers.comment.requests;

public record CommentCreateRequest(long articleId, String text) {
}

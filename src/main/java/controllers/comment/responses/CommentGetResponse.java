package controllers.comment.responses;

public record CommentGetResponse(long id, long articleId, String text) {
}

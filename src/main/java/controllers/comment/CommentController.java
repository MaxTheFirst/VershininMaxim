package controllers.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import controllers.Controller;
import controllers.article.ArticleController;
import controllers.comment.requests.CommentCreateRequest;
import controllers.comment.requests.CommentUpdateRequest;
import controllers.comment.responses.*;
import exceptions.CommentCreateException;
import exceptions.CommentDeleteException;
import exceptions.CommentNotFoundException;
import exceptions.CommentUpdateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.CommentService;
import spark.Request;
import spark.Response;
import spark.Service;
import types.comment.Comment;

public class CommentController implements Controller {
  private static final Logger LOG = LoggerFactory.getLogger(ArticleController.class);
  private final Service service;
  private final CommentService commentService;
  private final ObjectMapper objectMapper;

  public CommentController(Service service, CommentService commentService, ObjectMapper objectMapper) {
    this.service = service;
    this.commentService = commentService;
    this.objectMapper = objectMapper;
  }

  @Override
  public void initializeEndpoints() {
    createComment();
    getComment();
    updateComment();
    deleteComment();
  }

  private void createComment() {
    service.post("/api/comments", (Request request, Response response) -> {
      response.type("application/json");
      String body = request.body();
      CommentCreateRequest commentCreateRequest = objectMapper.readValue(body, CommentCreateRequest.class);
      try {
        long commentId = commentService.createComment(commentCreateRequest.articleId(), commentCreateRequest.text());
        response.status(201);
        LOG.debug("Comment is created");
        return objectMapper.writeValueAsString(new CommentCreateResponse(commentId));
      } catch (CommentCreateException e) {
        LOG.warn(e.getMessage(), e);
        response.status(400);
        return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
      }
    });
  }

  private void getComment() {
    service.get("api/comments/:commentId", (Request request, Response response) -> {
      response.type("aplication/json");
      long commentId = Long.parseLong(request.params("commentId"));
      try {
        Comment comment = commentService.findCommentById(commentId);
        response.status(201);
        LOG.debug("Comment is got");
        return objectMapper.writeValueAsString(new CommentGetResponse(comment.getId(), comment.getArticleId().getValue(), comment.getText()));
      } catch (CommentNotFoundException e) {
        LOG.warn(e.getMessage(), e);
        response.status(400);
        return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
      }
    });
  }

  private void updateComment() {
    service.put("/api/comments/:commentId", (Request request, Response response) -> {
      response.body("application/json");
      long commentId = Long.parseLong(request.params("commentId"));
      CommentUpdateRequest commentUpdateRequest = objectMapper.readValue(request.body(), CommentUpdateRequest.class);
      try {
        commentService.commentUpdate(commentId, commentUpdateRequest.text());
        response.status(201);
        LOG.debug("Comment is updated");
        return objectMapper.writeValueAsString(new CommentUpdateResponse(commentId));
      } catch (CommentUpdateException e) {
        LOG.warn("cannot update comment", e);
        response.status(400);
        return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
      }
    });
  }

  private void deleteComment() {
    service.delete("/api/comments/:commentId", (Request request, Response response) -> {
      response.body("application/json");
      long commentId = Long.parseLong(request.params("commentId"));
      try {
        commentService.deleteComment(commentId);
        response.status(201);
        LOG.debug("Comment is deleted");
        return objectMapper.writeValueAsString(new CommentDeleteResponse(commentId));
      } catch (CommentDeleteException e) {
        LOG.warn("cannot delete comment", e);
        response.status(400);
        return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
      }
    });
  }
}

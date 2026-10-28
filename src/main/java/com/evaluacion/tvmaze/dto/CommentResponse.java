package com.evaluacion.tvmaze.dto;

import com.evaluacion.tvmaze.document.Comment;

/**
 * Comentario tal como se incluye en las respuestas de los shows.
 */
public record CommentResponse(String comment, Integer rating) {

    public static CommentResponse from(Comment comment) {
        return new CommentResponse(comment.comment(), comment.rating());
    }
}

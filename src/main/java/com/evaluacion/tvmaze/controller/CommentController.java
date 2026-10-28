package com.evaluacion.tvmaze.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.evaluacion.tvmaze.document.Comment;
import com.evaluacion.tvmaze.dto.CommentRequest;
import com.evaluacion.tvmaze.dto.StatusResponse;
import com.evaluacion.tvmaze.service.CommentService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/api/shows/{show_id}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * Guarda una calificación (0-5) y un comentario para el show indicado.
     * Ejemplo: {@code POST /api/shows/139/comments} con cuerpo
     * {@code {"comment": "Muy buena", "rating": 5}}
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StatusResponse addComment(
            @PathVariable("show_id") @Positive long showId,
            @Valid @RequestBody CommentRequest request) {
        Comment saved = commentService.addComment(showId, request);
        return StatusResponse.success("Comentario guardado correctamente", saved.id());
    }
}

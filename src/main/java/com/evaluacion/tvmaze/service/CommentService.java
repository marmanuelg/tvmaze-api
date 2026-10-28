package com.evaluacion.tvmaze.service;

import org.springframework.stereotype.Service;

import com.evaluacion.tvmaze.document.Comment;
import com.evaluacion.tvmaze.dto.CommentRequest;
import com.evaluacion.tvmaze.repository.CommentRepository;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final ShowCacheService showCacheService;

    public CommentService(CommentRepository commentRepository, ShowCacheService showCacheService) {
        this.commentRepository = commentRepository;
        this.showCacheService = showCacheService;
    }

    /**
     * Guarda un comentario ligado a un show.
     *
     * @throws com.evaluacion.tvmaze.exception.ShowNotFoundException si el show no existe,
     *         para no guardar comentarios de ids inválidos
     */
    public Comment addComment(long showId, CommentRequest request) {
        showCacheService.getShow(showId);
        return commentRepository.save(Comment.create(showId, request.comment().trim(), request.rating()));
    }
}

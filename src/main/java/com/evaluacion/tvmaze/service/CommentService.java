package com.evaluacion.tvmaze.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.evaluacion.tvmaze.document.Comment;
import com.evaluacion.tvmaze.dto.CommentRequest;
import com.evaluacion.tvmaze.dto.CommentResponse;
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

    /**
     * Comentarios de un show, del más reciente al más antiguo.
     */
    public List<CommentResponse> findCommentsByShowId(long showId) {
        return commentRepository.findByShowIdOrderByCreatedAtDesc(showId).stream()
                .map(CommentResponse::from)
                .toList();
    }

    /**
     * Comentarios agrupados por id de show, del más reciente al más antiguo.
     * Los shows sin comentarios no aparecen en el mapa.
     */
    public Map<Long, List<CommentResponse>> findCommentsByShowIds(Collection<Long> showIds) {
        if (showIds.isEmpty()) {
            return Map.of();
        }
        return commentRepository.findByShowIdInOrderByCreatedAtDesc(showIds).stream()
                .collect(Collectors.groupingBy(
                        Comment::showId,
                        Collectors.mapping(CommentResponse::from, Collectors.toList())));
    }
}

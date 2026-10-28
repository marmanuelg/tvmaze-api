package com.evaluacion.tvmaze.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.evaluacion.tvmaze.document.Comment;

public interface CommentRepository extends MongoRepository<Comment, String> {

    List<Comment> findByShowIdOrderByCreatedAtDesc(Long showId);

    /** Comentarios de varios shows en una sola consulta (evita una consulta por show). */
    List<Comment> findByShowIdInOrderByCreatedAtDesc(Collection<Long> showIds);
}

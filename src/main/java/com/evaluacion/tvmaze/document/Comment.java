package com.evaluacion.tvmaze.document;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Calificación y comentario de un usuario sobre un show.
 */
@Document(collection = "comments")
public record Comment(
        @Id String id,
        @Indexed Long showId,
        String comment,
        Integer rating,
        Instant createdAt) {

    public static Comment create(long showId, String comment, int rating) {
        return new Comment(null, showId, comment, rating, Instant.now());
    }
}

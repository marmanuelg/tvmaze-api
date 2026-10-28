package com.evaluacion.tvmaze.dto;

import java.util.List;

/**
 * Show resumido que devuelve el endpoint de búsqueda, junto con sus comentarios guardados.
 */
public record ShowSummaryResponse(
        Long id,
        String name,
        String channel,
        String summary,
        List<String> genres,
        List<CommentResponse> comments) {
}

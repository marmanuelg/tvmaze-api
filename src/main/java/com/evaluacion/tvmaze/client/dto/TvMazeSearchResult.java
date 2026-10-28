package com.evaluacion.tvmaze.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Elemento de la respuesta de {@code /search/shows}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record TvMazeSearchResult(Double score, TvMazeShow show) {
}

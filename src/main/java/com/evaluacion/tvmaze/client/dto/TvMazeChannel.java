package com.evaluacion.tvmaze.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Cadena de televisión (network) o canal web (webChannel) de TV Maze.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record TvMazeChannel(String name) {
}

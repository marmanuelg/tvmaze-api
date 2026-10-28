package com.evaluacion.tvmaze.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuración del API externo de TV Maze (prefijo {@code tvmaze.api}).
 */
@ConfigurationProperties(prefix = "tvmaze.api")
public record TvMazeProperties(String baseUrl, Duration connectTimeout, Duration readTimeout) {
}

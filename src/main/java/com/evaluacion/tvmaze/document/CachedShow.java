package com.evaluacion.tvmaze.document;

import java.time.Instant;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Copia en MongoDB del show completo de TV Maze. Se usa como caché para no
 * volver a consultar el API externo con un id ya conocido.
 *
 * @param id       id del show en TV Maze (se usa como _id del documento)
 * @param data     objeto show tal como lo devolvió TV Maze
 * @param cachedAt fecha en que se guardó
 */
@Document(collection = "shows")
public record CachedShow(@Id Long id, Map<String, Object> data, Instant cachedAt) {
}

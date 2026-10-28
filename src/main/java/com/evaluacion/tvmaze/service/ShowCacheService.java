package com.evaluacion.tvmaze.service;

import java.time.Instant;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.evaluacion.tvmaze.client.TvMazeClient;
import com.evaluacion.tvmaze.document.CachedShow;
import com.evaluacion.tvmaze.repository.CachedShowRepository;

/**
 * Obtiene shows aplicando una caché en MongoDB: primero busca el id en la colección
 * {@code shows}; si no está, consulta TV Maze y guarda el resultado antes de devolverlo.
 */
@Service
public class ShowCacheService {

    private static final Logger log = LoggerFactory.getLogger(ShowCacheService.class);

    private final CachedShowRepository cachedShowRepository;
    private final TvMazeClient tvMazeClient;

    public ShowCacheService(CachedShowRepository cachedShowRepository, TvMazeClient tvMazeClient) {
        this.cachedShowRepository = cachedShowRepository;
        this.tvMazeClient = tvMazeClient;
    }

    public Map<String, Object> getShow(long showId) {
        return cachedShowRepository.findById(showId)
                .map(cached -> {
                    log.debug("Show {} obtenido de la caché", showId);
                    return cached.data();
                })
                .orElseGet(() -> fetchAndCache(showId));
    }

    private Map<String, Object> fetchAndCache(long showId) {
        log.debug("Show {} no está en caché; se consulta TV Maze", showId);
        Map<String, Object> show = tvMazeClient.getShowById(showId);
        cachedShowRepository.save(new CachedShow(showId, show, Instant.now()));
        return show;
    }
}

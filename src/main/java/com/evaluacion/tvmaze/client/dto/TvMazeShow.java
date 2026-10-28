package com.evaluacion.tvmaze.client.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Campos de un show de TV Maze que necesita el endpoint de búsqueda.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record TvMazeShow(
        Long id,
        String name,
        TvMazeChannel network,
        TvMazeChannel webChannel,
        String summary,
        List<String> genres) {

    /**
     * Nombre del canal: el de la cadena de TV si existe; si no, el del canal web.
     */
    public String channelName() {
        if (network != null && network.name() != null) {
            return network.name();
        }
        return webChannel != null ? webChannel.name() : null;
    }
}

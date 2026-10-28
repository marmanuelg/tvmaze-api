package com.evaluacion.tvmaze.client;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import com.evaluacion.tvmaze.client.dto.TvMazeSearchResult;
import com.evaluacion.tvmaze.exception.TvMazeApiException;

/**
 * Cliente HTTP del API público de TV Maze.
 */
@Component
public class TvMazeClient {

    private static final ParameterizedTypeReference<List<TvMazeSearchResult>> SEARCH_RESULTS_TYPE =
            new ParameterizedTypeReference<>() { };

    private final RestClient restClient;

    public TvMazeClient(RestClient tvMazeRestClient) {
        this.restClient = tvMazeRestClient;
    }

    public List<TvMazeSearchResult> searchShows(String query) {
        try {
            List<TvMazeSearchResult> results = restClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/search/shows").queryParam("q", query).build())
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, response) -> {
                        throw new TvMazeApiException("TV Maze respondió con estado " + response.getStatusCode());
                    })
                    .body(SEARCH_RESULTS_TYPE);
            return results != null ? results : List.of();
        } catch (ResourceAccessException ex) {
            throw new TvMazeApiException("No fue posible conectarse con TV Maze", ex);
        }
    }
}

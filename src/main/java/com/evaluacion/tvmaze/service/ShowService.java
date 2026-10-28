package com.evaluacion.tvmaze.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.evaluacion.tvmaze.client.TvMazeClient;
import com.evaluacion.tvmaze.client.dto.TvMazeSearchResult;
import com.evaluacion.tvmaze.client.dto.TvMazeShow;
import com.evaluacion.tvmaze.dto.ShowSummaryResponse;

@Service
public class ShowService {

    private final TvMazeClient tvMazeClient;
    private final ShowCacheService showCacheService;

    public ShowService(TvMazeClient tvMazeClient, ShowCacheService showCacheService) {
        this.tvMazeClient = tvMazeClient;
        this.showCacheService = showCacheService;
    }

    public List<ShowSummaryResponse> searchShows(String query) {
        return tvMazeClient.searchShows(query).stream()
                .map(TvMazeSearchResult::show)
                .filter(Objects::nonNull)
                .map(this::toSummary)
                .toList();
    }

    public Map<String, Object> getShowById(long showId) {
        return showCacheService.getShow(showId);
    }

    private ShowSummaryResponse toSummary(TvMazeShow show) {
        List<String> genres = show.genres() != null ? show.genres() : List.of();
        return new ShowSummaryResponse(show.id(), show.name(), show.channelName(), show.summary(), genres);
    }
}

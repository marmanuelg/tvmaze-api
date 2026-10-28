package com.evaluacion.tvmaze.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.evaluacion.tvmaze.client.TvMazeClient;
import com.evaluacion.tvmaze.client.dto.TvMazeSearchResult;
import com.evaluacion.tvmaze.client.dto.TvMazeShow;
import com.evaluacion.tvmaze.dto.CommentResponse;
import com.evaluacion.tvmaze.dto.ShowSummaryResponse;

@Service
public class ShowService {

    private final TvMazeClient tvMazeClient;
    private final ShowCacheService showCacheService;
    private final CommentService commentService;

    public ShowService(TvMazeClient tvMazeClient, ShowCacheService showCacheService, CommentService commentService) {
        this.tvMazeClient = tvMazeClient;
        this.showCacheService = showCacheService;
        this.commentService = commentService;
    }

    public List<ShowSummaryResponse> searchShows(String query) {
        List<TvMazeShow> shows = tvMazeClient.searchShows(query).stream()
                .map(TvMazeSearchResult::show)
                .filter(Objects::nonNull)
                .toList();

        List<Long> showIds = shows.stream().map(TvMazeShow::id).toList();
        Map<Long, List<CommentResponse>> commentsByShow = commentService.findCommentsByShowIds(showIds);

        return shows.stream()
                .map(show -> toSummary(show, commentsByShow.getOrDefault(show.id(), List.of())))
                .toList();
    }

    public Map<String, Object> getShowById(long showId) {
        return showCacheService.getShow(showId);
    }

    private ShowSummaryResponse toSummary(TvMazeShow show, List<CommentResponse> comments) {
        List<String> genres = show.genres() != null ? show.genres() : List.of();
        return new ShowSummaryResponse(show.id(), show.name(), show.channelName(), show.summary(), genres, comments);
    }
}

package com.evaluacion.tvmaze.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.evaluacion.tvmaze.client.TvMazeClient;
import com.evaluacion.tvmaze.client.dto.TvMazeChannel;
import com.evaluacion.tvmaze.client.dto.TvMazeSearchResult;
import com.evaluacion.tvmaze.client.dto.TvMazeShow;
import com.evaluacion.tvmaze.dto.CommentResponse;
import com.evaluacion.tvmaze.dto.ShowSummaryResponse;

@ExtendWith(MockitoExtension.class)
class ShowServiceTest {

    @Mock
    private TvMazeClient tvMazeClient;

    @Mock
    private ShowCacheService showCacheService;

    @Mock
    private CommentService commentService;

    @InjectMocks
    private ShowService showService;

    @Test
    void searchUsesNetworkOrWebChannelAndAddsComments() {
        TvMazeShow tvShow = new TvMazeShow(1L, "Girls", new TvMazeChannel("HBO"), null, "<p>A</p>", List.of("Drama"));
        TvMazeShow webShow = new TvMazeShow(2L, "Web Show", null, new TvMazeChannel("Netflix"), "<p>B</p>", null);
        when(tvMazeClient.searchShows("girls")).thenReturn(List.of(
                new TvMazeSearchResult(0.9, tvShow), new TvMazeSearchResult(0.5, webShow)));
        when(commentService.findCommentsByShowIds(List.of(1L, 2L)))
                .thenReturn(Map.of(1L, List.of(new CommentResponse("Excelente", 5))));

        List<ShowSummaryResponse> result = showService.searchShows("girls");

        assertThat(result).hasSize(2);
        assertThat(result.get(0).channel()).isEqualTo("HBO");
        assertThat(result.get(0).comments()).containsExactly(new CommentResponse("Excelente", 5));
        assertThat(result.get(1).channel()).isEqualTo("Netflix");
        assertThat(result.get(1).genres()).isEmpty();
        assertThat(result.get(1).comments()).isEmpty();
    }

    @Test
    void getShowAddsCommentsWithoutModifyingCachedObject() {
        Map<String, Object> cached = Map.of("id", 1, "name", "Under the Dome");
        List<CommentResponse> comments = List.of(new CommentResponse("Buena", 4));
        when(showCacheService.getShow(1)).thenReturn(cached);
        when(commentService.findCommentsByShowId(1)).thenReturn(comments);

        Map<String, Object> result = showService.getShowById(1);

        assertThat(result).containsEntry("name", "Under the Dome").containsEntry("comments", comments);
        assertThat(cached).doesNotContainKey("comments");
    }
}

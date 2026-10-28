package com.evaluacion.tvmaze.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.evaluacion.tvmaze.client.TvMazeClient;
import com.evaluacion.tvmaze.document.CachedShow;
import com.evaluacion.tvmaze.repository.CachedShowRepository;

@ExtendWith(MockitoExtension.class)
class ShowCacheServiceTest {

    @Mock
    private CachedShowRepository cachedShowRepository;

    @Mock
    private TvMazeClient tvMazeClient;

    @InjectMocks
    private ShowCacheService showCacheService;

    @Test
    void returnsCachedShowWithoutCallingTvMaze() {
        Map<String, Object> cached = Map.of("id", 1, "name", "Under the Dome");
        when(cachedShowRepository.findById(1L)).thenReturn(Optional.of(new CachedShow(1L, cached, Instant.now())));

        Map<String, Object> result = showCacheService.getShow(1);

        assertThat(result).isEqualTo(cached);
        verify(tvMazeClient, never()).getShowById(anyLong());
        verify(cachedShowRepository, never()).save(any());
    }

    @Test
    void fetchesFromTvMazeAndSavesWhenNotCached() {
        Map<String, Object> fromApi = Map.of("id", 1, "name", "Under the Dome");
        when(cachedShowRepository.findById(1L)).thenReturn(Optional.empty());
        when(tvMazeClient.getShowById(1)).thenReturn(fromApi);

        Map<String, Object> result = showCacheService.getShow(1);

        assertThat(result).isEqualTo(fromApi);
        ArgumentCaptor<CachedShow> saved = ArgumentCaptor.forClass(CachedShow.class);
        verify(cachedShowRepository).save(saved.capture());
        assertThat(saved.getValue().id()).isEqualTo(1L);
        assertThat(saved.getValue().data()).isEqualTo(fromApi);
    }
}

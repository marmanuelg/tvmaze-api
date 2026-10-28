package com.evaluacion.tvmaze.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.evaluacion.tvmaze.dto.CommentResponse;
import com.evaluacion.tvmaze.dto.ShowSummaryResponse;
import com.evaluacion.tvmaze.exception.ShowNotFoundException;
import com.evaluacion.tvmaze.exception.TvMazeApiException;
import com.evaluacion.tvmaze.service.ShowService;

@WebMvcTest(ShowController.class)
class ShowControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ShowService showService;

    @Test
    void searchReturnsShowsWithComments() throws Exception {
        when(showService.searchShows("girls")).thenReturn(List.of(new ShowSummaryResponse(
                139L, "Girls", "HBO", "<p>Resumen</p>", List.of("Drama"),
                List.of(new CommentResponse("Excelente", 5)))));

        mockMvc.perform(get("/api/shows/search").param("search_query", "girls"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(139))
                .andExpect(jsonPath("$[0].channel").value("HBO"))
                .andExpect(jsonPath("$[0].comments[0].rating").value(5));
    }

    @Test
    void searchWithoutQueryReturns400() throws Exception {
        mockMvc.perform(get("/api/shows/search"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void unknownShowReturns404() throws Exception {
        when(showService.getShowById(999L)).thenThrow(new ShowNotFoundException(999));

        mockMvc.perform(get("/api/shows/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"));
    }

    @Test
    void nonNumericIdReturns400() throws Exception {
        mockMvc.perform(get("/api/shows/abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void tvMazeFailureReturns502() throws Exception {
        when(showService.getShowById(1L)).thenThrow(new TvMazeApiException("caído"));

        mockMvc.perform(get("/api/shows/1"))
                .andExpect(status().isBadGateway());
    }
}

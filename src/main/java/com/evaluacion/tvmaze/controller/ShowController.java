package com.evaluacion.tvmaze.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.evaluacion.tvmaze.dto.ShowSummaryResponse;
import com.evaluacion.tvmaze.service.ShowService;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/api/shows")
public class ShowController {

    private final ShowService showService;

    public ShowController(ShowService showService) {
        this.showService = showService;
    }

    /**
     * Busca shows por nombre. Ejemplo: {@code GET /api/shows/search?search_query=girls}
     */
    @GetMapping("/search")
    public List<ShowSummaryResponse> searchShows(
            @RequestParam("search_query") @NotBlank String searchQuery) {
        return showService.searchShows(searchQuery.trim());
    }

    /**
     * Obtiene el show completo por su id. Ejemplo: {@code GET /api/shows/139}
     */
    @GetMapping("/{show_id}")
    public Map<String, Object> getShow(@PathVariable("show_id") @Positive long showId) {
        return showService.getShowById(showId);
    }
}

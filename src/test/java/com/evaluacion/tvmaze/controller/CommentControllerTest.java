package com.evaluacion.tvmaze.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.evaluacion.tvmaze.document.Comment;
import com.evaluacion.tvmaze.dto.CommentRequest;
import com.evaluacion.tvmaze.exception.ShowNotFoundException;
import com.evaluacion.tvmaze.service.CommentService;

@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CommentService commentService;

    @Test
    void validCommentReturns201WithStatus() throws Exception {
        when(commentService.addComment(eq(139L), any(CommentRequest.class)))
                .thenReturn(new Comment("abc123", 139L, "Muy buena", 5, Instant.now()));

        mockMvc.perform(post("/api/shows/139/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"comment\": \"Muy buena\", \"rating\": 5}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.id").value("abc123"));
    }

    @Test
    void ratingOutOfRangeReturns400() throws Exception {
        mockMvc.perform(post("/api/shows/139/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"comment\": \"Muy buena\", \"rating\": 6}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"));

        verify(commentService, never()).addComment(anyLong(), any());
    }

    @Test
    void missingCommentReturns400() throws Exception {
        mockMvc.perform(post("/api/shows/139/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rating\": 3}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void unknownShowReturns404() throws Exception {
        when(commentService.addComment(eq(999L), any(CommentRequest.class)))
                .thenThrow(new ShowNotFoundException(999));

        mockMvc.perform(post("/api/shows/999/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"comment\": \"Hola\", \"rating\": 3}"))
                .andExpect(status().isNotFound());
    }
}

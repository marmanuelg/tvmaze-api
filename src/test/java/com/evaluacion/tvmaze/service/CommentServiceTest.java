package com.evaluacion.tvmaze.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.evaluacion.tvmaze.document.Comment;
import com.evaluacion.tvmaze.dto.CommentRequest;
import com.evaluacion.tvmaze.dto.CommentResponse;
import com.evaluacion.tvmaze.exception.ShowNotFoundException;
import com.evaluacion.tvmaze.repository.CommentRepository;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ShowCacheService showCacheService;

    @InjectMocks
    private CommentService commentService;

    @Test
    void savesCommentLinkedToShow() {
        when(showCacheService.getShow(139)).thenReturn(Map.of("id", 139));
        when(commentRepository.save(any(Comment.class)))
                .thenAnswer(invocation -> withId(invocation.getArgument(0), "abc123"));

        Comment saved = commentService.addComment(139, new CommentRequest("  Muy buena  ", 5));

        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository).save(captor.capture());
        assertThat(captor.getValue().showId()).isEqualTo(139L);
        assertThat(captor.getValue().comment()).isEqualTo("Muy buena");
        assertThat(captor.getValue().rating()).isEqualTo(5);
        assertThat(saved.id()).isEqualTo("abc123");
    }

    @Test
    void doesNotSaveCommentWhenShowDoesNotExist() {
        when(showCacheService.getShow(999)).thenThrow(new ShowNotFoundException(999));

        assertThatThrownBy(() -> commentService.addComment(999, new CommentRequest("Hola", 3)))
                .isInstanceOf(ShowNotFoundException.class);
        verify(commentRepository, never()).save(any());
    }

    @Test
    void groupsCommentsByShowId() {
        when(commentRepository.findByShowIdInOrderByCreatedAtDesc(List.of(1L, 2L))).thenReturn(List.of(
                new Comment("a", 1L, "Buena", 4, Instant.now()),
                new Comment("b", 1L, "Regular", 3, Instant.now()),
                new Comment("c", 2L, "Mala", 1, Instant.now())));

        Map<Long, List<CommentResponse>> result = commentService.findCommentsByShowIds(List.of(1L, 2L));

        assertThat(result.get(1L)).containsExactly(
                new CommentResponse("Buena", 4), new CommentResponse("Regular", 3));
        assertThat(result.get(2L)).containsExactly(new CommentResponse("Mala", 1));
    }

    @Test
    void emptyIdListDoesNotQueryDatabase() {
        assertThat(commentService.findCommentsByShowIds(List.of())).isEmpty();
        verify(commentRepository, never()).findByShowIdInOrderByCreatedAtDesc(any());
    }

    private static Comment withId(Comment comment, String id) {
        return new Comment(id, comment.showId(), comment.comment(), comment.rating(), comment.createdAt());
    }
}

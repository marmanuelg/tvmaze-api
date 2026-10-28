package com.evaluacion.tvmaze.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Cuerpo de la petición para registrar un comentario.
 */
public record CommentRequest(
        @NotBlank(message = "comment es obligatorio")
        @Size(max = 1000, message = "comment no puede exceder 1000 caracteres")
        String comment,

        @NotNull(message = "rating es obligatorio")
        @Min(value = 0, message = "rating debe estar entre 0 y 5")
        @Max(value = 5, message = "rating debe estar entre 0 y 5")
        Integer rating) {
}

package com.evaluacion.tvmaze.exception;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import com.evaluacion.tvmaze.dto.ApiError;

/**
 * Traduce las excepciones de la aplicación a respuestas HTTP con un formato uniforme.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(TvMazeApiException.class)
    public ResponseEntity<ApiError> handleTvMazeError(TvMazeApiException ex) {
        log.error("Error al consumir TV Maze: {}", ex.getMessage(), ex);
        return build(HttpStatus.BAD_GATEWAY, "El servicio de TV Maze no está disponible en este momento");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiError> handleMissingParameter(MissingServletRequestParameterException ex) {
        return build(HttpStatus.BAD_REQUEST, "Falta el parámetro obligatorio '" + ex.getParameterName() + "'");
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiError> handleMethodValidation(HandlerMethodValidationException ex) {
        List<String> details = ex.getAllErrors().stream()
                .map(MessageSourceResolvable::getDefaultMessage)
                .toList();
        return build(HttpStatus.BAD_REQUEST, "Parámetros inválidos", details);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex) {
        // Excepciones propias de Spring MVC (ruta inexistente, método no permitido, etc.)
        // ya traen el código HTTP correcto: se respeta en lugar de responder 500.
        if (ex instanceof ErrorResponse springError) {
            HttpStatusCode status = springError.getStatusCode();
            String message = springError.getBody().getDetail();
            return build(status, message != null ? message : "Solicitud inválida");
        }
        log.error("Error inesperado", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error inesperado");
    }

    private ResponseEntity<ApiError> build(HttpStatusCode status, String message) {
        return build(status, message, List.of());
    }

    private ResponseEntity<ApiError> build(HttpStatusCode status, String message, List<String> details) {
        return ResponseEntity.status(status).body(ApiError.of(status.value(), message, details));
    }
}

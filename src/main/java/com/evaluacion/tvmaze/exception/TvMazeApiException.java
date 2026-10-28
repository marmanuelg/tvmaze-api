package com.evaluacion.tvmaze.exception;

/**
 * Error al comunicarse con el API de TV Maze (no disponible, timeout o respuesta inesperada).
 */
public class TvMazeApiException extends RuntimeException {

    public TvMazeApiException(String message) {
        super(message);
    }

    public TvMazeApiException(String message, Throwable cause) {
        super(message, cause);
    }
}

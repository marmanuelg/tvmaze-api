package com.evaluacion.tvmaze.exception;

/**
 * El show solicitado no existe en TV Maze.
 */
public class ShowNotFoundException extends RuntimeException {

    public ShowNotFoundException(long showId) {
        super("No se encontró el show con id " + showId);
    }
}

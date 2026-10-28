package com.evaluacion.tvmaze.dto;

/**
 * Respuesta con el estado de una operación de escritura.
 */
public record StatusResponse(String status, String message, String id) {

    public static StatusResponse success(String message, String id) {
        return new StatusResponse("success", message, id);
    }
}

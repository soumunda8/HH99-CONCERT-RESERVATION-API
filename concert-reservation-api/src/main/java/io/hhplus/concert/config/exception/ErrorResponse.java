package io.hhplus.concert.config.exception;

public record ErrorResponse(
        String code,
        String message
) {
}

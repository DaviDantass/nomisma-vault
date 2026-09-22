package com.davidantasdev.nomismavault.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ApiErrorResponse> handleAccessDeniedException(
      AccessDeniedException ex, HttpServletRequest request) {

    log.warn("Access denied: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(
            buildErrorResponse(
                HttpStatus.FORBIDDEN, "Acesso negado", request.getRequestURI(), null));
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ApiErrorResponse> handleResourceNotFoundException(
      ResourceNotFoundException ex, HttpServletRequest request) {

    log.warn("Resource not found: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(
            buildErrorResponse(
                HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI(), null));
  }

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ApiErrorResponse> handleBusinessException(
      BusinessException ex, HttpServletRequest request) {

    log.warn("Business exception: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(
            buildErrorResponse(
                HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI(), null));
  }

  @ExceptionHandler(MarketDataNotFoundException.class)
  public ResponseEntity<ApiErrorResponse> handleMarketDataNotFound(
      MarketDataNotFoundException ex, HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(
            buildErrorResponse(
                HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI(), null));
  }

  @ExceptionHandler(MarketDataRateLimitException.class)
  public ResponseEntity<ApiErrorResponse> handleMarketDataRateLimit(
      MarketDataRateLimitException ex, HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
        .body(
            buildErrorResponse(
                HttpStatus.TOO_MANY_REQUESTS, ex.getMessage(), request.getRequestURI(), null));
  }

  @ExceptionHandler(MarketDataUnavailableException.class)
  public ResponseEntity<ApiErrorResponse> handleMarketDataUnavailable(
      MarketDataUnavailableException ex, HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
        .body(
            buildErrorResponse(
                HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage(), request.getRequestURI(), null));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(
      IllegalArgumentException ex, HttpServletRequest request) {

    log.warn("Illegal argument: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(
            buildErrorResponse(
                HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI(), null));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiErrorResponse> handleValidationException(
      MethodArgumentNotValidException ex, HttpServletRequest request) {

    List<ApiErrorResponse.FieldError> fieldErrors =
        ex.getBindingResult().getFieldErrors().stream()
            .map(err -> new ApiErrorResponse.FieldError(err.getField(), err.getDefaultMessage()))
            .collect(Collectors.toList());

    log.warn("Validation error on fields: {}", fieldErrors);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(
            buildErrorResponse(
                HttpStatus.BAD_REQUEST, "Erro de validação", request.getRequestURI(), fieldErrors));
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ApiErrorResponse> handleConstraintViolationException(
      ConstraintViolationException ex, HttpServletRequest request) {

    List<ApiErrorResponse.FieldError> fieldErrors =
        ex.getConstraintViolations().stream()
            .map(
                violation -> {
                  String fieldName = extractFieldName(violation.getPropertyPath().toString());
                  return new ApiErrorResponse.FieldError(fieldName, violation.getMessage());
                })
            .collect(Collectors.toList());

    log.warn("Constraint violation on fields: {}", fieldErrors);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(
            buildErrorResponse(
                HttpStatus.BAD_REQUEST, "Erro de validação", request.getRequestURI(), fieldErrors));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiErrorResponse> handleGenericException(
      Exception ex, HttpServletRequest request) {

    log.error("Erro interno no servidor", ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(
            buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno do servidor",
                request.getRequestURI(),
                null));
  }

  private ApiErrorResponse buildErrorResponse(
      HttpStatus status,
      String message,
      String path,
      List<ApiErrorResponse.FieldError> fieldErrors) {
    return new ApiErrorResponse(
        LocalDateTime.now(), status.value(), status.getReasonPhrase(), message, path, fieldErrors);
  }

  private String extractFieldName(String propertyPath) {
    if (propertyPath == null || propertyPath.isEmpty()) {
      return propertyPath;
    }
    int lastDotIndex = propertyPath.lastIndexOf('.');
    return lastDotIndex >= 0 ? propertyPath.substring(lastDotIndex + 1) : propertyPath;
  }
}

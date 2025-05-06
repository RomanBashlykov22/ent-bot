package kz.roman.enttgbot.expection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.security.auth.message.AuthException;
import jakarta.validation.ConstraintViolationException;
import kz.roman.enttgbot.model.ApiResponse;
import kz.roman.enttgbot.model.ErrorDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.net.ConnectException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ObjectMapper objectMapper;

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ApiResponse<?>> handleAuthenticationException(AuthException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiResponse.error(List.of(
                        new ErrorDetail(
                                "ERROR:UNAUTHORIZED:ACCESS_DENIED",
                                "Access denied",
                                List.of(ErrorDetail.FieldError.builder()
                                        .message(ex.getMessage())
                                        .type("Unauthorized").build()
                                ))
                ))
        );
    }

//    @ExceptionHandler(AuthorizationDeniedException.class)
//    public ResponseEntity<ApiResponse<?>> handleAuthorizationDenied(AuthorizationDeniedException ex) {
//        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
//                ApiResponse.error(List.of(
//                        new ErrorDetail(
//                                "ERROR:FORBIDDEN:ACCESS_RESTRICTED",
//                                "Access forbidden",
//                                List.of(ErrorDetail.FieldError.builder()
//                                        .message(ex.getMessage())
//                                        .type("Forbidden").build()
//                                ))
//                ))
//        );
//    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(List.of(
                        new ErrorDetail("ERROR:NOT_FOUND:RESOURCE", "Requested resource not found",
                                List.of(ErrorDetail.FieldError.builder()
                                        .message(ex.getMessage())
                                        .type("Invalid URL").build()
                                ))
                )));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleEntityNotFoundException(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(List.of(
                        new ErrorDetail("ERROR:NOT_FOUND:ENTITY", "Requested entity does not exist",
                                List.of(ErrorDetail.FieldError.builder()
                                        .message(ex.getMessage())
                                        .type("Not Found").build()
                                ))
                )));
    }

//    @ExceptionHandler(UnauthorizedException.class)
//    public ResponseEntity<ApiResponse<?>> handleUnauthorizedException(UnauthorizedException ex) {
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                .body(ApiResponse.error(List.of(
//                        new ErrorDetail("ERROR:UNAUTHORIZED:ACCESS_DENIED", "Access denied",
//                                List.of(ErrorDetail.FieldError.builder()
//                                        .message(ex.getMessage())
//                                        .type("Unauthorized").build()
//                                ))
//                )));
//    }

    @ExceptionHandler({ConnectException.class})
    public ResponseEntity<ApiResponse<?>> handleConnectionException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(List.of(
                        new ErrorDetail("ERROR:INTERNAL:CONNECTION_REFUSED", "Connection is closed",
                                List.of(ErrorDetail.FieldError.builder()
                                        .message(ex.getMessage())
                                        .type("Connection refused").build()
                                ))
                )));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(MethodArgumentNotValidException ex) {
        List<ErrorDetail.FieldError> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> ErrorDetail.FieldError.builder()
                        .field(fieldError.getField())
                        .message(Objects.requireNonNull(fieldError.getDefaultMessage()))
                        .type(fieldError.getCode())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(List.of(
                        new ErrorDetail("ERROR:BAD_REQUEST:VALIDATION_ERROR", "Validation failed", fieldErrors)
                )));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<?>> handleConstraintViolationException(ConstraintViolationException ex) {
        List<ErrorDetail.FieldError> fieldErrors = ex.getConstraintViolations().stream()
                .map(violation -> ErrorDetail.FieldError.builder()
                        .field(violation.getPropertyPath().toString())
                        .message(violation.getMessage())
                        .type(violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName())
                        .build()
                )
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(List.of(
                        new ErrorDetail("ERROR:BAD_REQUEST:CONSTRAINT_VIOLATION", "Constraint violation", fieldErrors)
                )));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<?>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(List.of(
                        new ErrorDetail("ERROR:BAD_REQUEST:INVALID_REQUEST_BODY", "Malformed or invalid request body",
                                List.of(ErrorDetail.FieldError.builder()
                                        .message(ex.getMessage())
                                        .type("Invalid JSON format").build()
                                ))
                )));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<?>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(List.of(
                        new ErrorDetail("ERROR:CONFLICT:DATA_INTEGRITY", "Database constraint violation",
                                List.of(ErrorDetail.FieldError.builder()
                                        .message(ex.getMessage())
                                        .type("Constraint violation").build()
                                ))
                )));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<?>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiResponse.error(List.of(
                        new ErrorDetail("ERROR:METHOD_NOT_ALLOWED:UNSUPPORTED", "Request method is not supported",
                                List.of(ErrorDetail.FieldError.builder()
                                        .message(ex.getMessage())
                                        .type("Unsupported HTTP method").build()
                                ))
                )));
    }

    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<ApiResponse<?>> handleTransactionException(TransactionSystemException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(List.of(
                        new ErrorDetail("ERROR:BAD_REQUEST:TRANSACTION", "Transaction processing failed",
                                List.of(ErrorDetail.FieldError.builder()
                                        .message(ex.getMessage())
                                        .type("Transaction Issue").build()
                                ))
                )));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<?>> handleMissingParamsException(MissingServletRequestParameterException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(List.of(
                        new ErrorDetail("ERROR:BAD_REQUEST:MISSING_PARAMETER", "Required request parameter is missing",
                                List.of(ErrorDetail.FieldError.builder()
                                        .field(ex.getParameterName())
                                        .message("Parameter is missing")
                                        .type("Required")
                                        .build()))
                )));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(List.of(
                        new ErrorDetail("ERROR:BAD_REQUEST:ILLEGAL_ARGUMENT", "Invalid argument provided",
                                List.of(ErrorDetail.FieldError.builder()
                                        .message(ex.getMessage())
                                        .type("Illegal argument").build()
                                ))
                )));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalStateException(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(List.of(
                        new ErrorDetail("ERROR:BAD_REQUEST:ILLEGAL_STATE", "Invalid state encountered",
                                List.of(ErrorDetail.FieldError.builder()
                                        .message(ex.getMessage())
                                        .type("Illegal state").build()
                                ))
                )));
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiResponse<?>> handleNullPointerException(NullPointerException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(List.of(
                        new ErrorDetail("ERROR:NULL_POINTER", "Unexpected null value encountered",
                                List.of(ErrorDetail.FieldError.builder()
                                        .message(ex.getMessage())
                                        .type("Null pointer").build()
                                ))
                )));
    }


    @ExceptionHandler({ExecutionException.class, InterruptedException.class})
    public ResponseEntity<ApiResponse<?>> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(List.of(
                        new ErrorDetail("ERROR:INTERNAL:EXECUTION_INTERRUPTED", "Thread is interrupted",
                                List.of(ErrorDetail.FieldError.builder()
                                        .message(ex.getMessage())
                                        .type("Thread interrupted").build()
                                ))
                )));
    }
}
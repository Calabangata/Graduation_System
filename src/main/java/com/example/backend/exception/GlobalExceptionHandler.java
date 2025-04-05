package com.example.backend.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.security.auth.login.AccountLockedException;
import java.util.Map;
import java.util.function.Function;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<String> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleSecurityException(Exception exception) {
        ProblemDetail errorDetail = null;

        // TODO send this stack trace to an observability tool
        exception.printStackTrace();

        Map<Class<? extends Exception>, Function<Exception, ProblemDetail>> exceptionHandlers = Map.of(
                BadCredentialsException.class, e -> createProblemDetail(HttpStatus.UNAUTHORIZED.value(), e, "The username or password is incorrect"),
                AccountLockedException.class, e -> createProblemDetail(HttpStatus.LOCKED.value(), e, "The Account is locked"),
                AccessDeniedException.class, e -> createProblemDetail(HttpStatus.FORBIDDEN.value(), e, "You are not authorized to access this resource"),
                SignatureException.class, e -> createProblemDetail(HttpStatus.BAD_REQUEST.value(), e, "The JWT signature is invalid"),
                ExpiredJwtException.class, e -> createProblemDetail(HttpStatus.UNAUTHORIZED.value(), e, "The JWT token has expired")
        );

        return exceptionHandlers.entrySet().stream()
                .filter(entry -> entry.getKey().isAssignableFrom(exception.getClass()))
                .findFirst()
                .map(entry -> entry.getValue().apply(exception))
                .orElseGet(() -> createProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception, "An unexpected error occurred"));

    }

    private ProblemDetail createProblemDetail(int statusCode, Exception exception, String description) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.valueOf(statusCode),
                exception.getMessage()
        );
        problemDetail.setProperty("description", description);
        return problemDetail;
    }
}

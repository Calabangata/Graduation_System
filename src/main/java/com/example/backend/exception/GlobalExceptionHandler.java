package com.example.backend.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.security.auth.login.AccountLockedException;
import java.util.Map;
import java.util.function.Function;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(ResourceNotFoundException ex) {
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

    @ExceptionHandler(ForbiddenActionException.class)
    public ResponseEntity<ProblemDetail> handleForbiddenActionException(ForbiddenActionException exception) {
        ProblemDetail problemDetail = createProblemDetail(HttpStatus.FORBIDDEN.value(), exception, "You are not allowed to perform this action");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(problemDetail);
    }

    @ExceptionHandler(DuplicateActionException.class)
    public ResponseEntity<ProblemDetail> handleDuplicateActionException(DuplicateActionException exception) {
        ProblemDetail problemDetail = createProblemDetail(HttpStatus.CONFLICT.value(), exception, "This action has already been performed");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problemDetail);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ProblemDetail> handleConflictException(ConflictException exception) {
        ProblemDetail problemDetail = createProblemDetail(HttpStatus.CONFLICT.value(), exception, "There is a conflict with the current state of the resource");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problemDetail);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ProblemDetail> handleDisabledException(DisabledException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "Account is deactivated");
        detail.setProperty("description", "Please contact an administrator to activate your account.");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(detail);
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

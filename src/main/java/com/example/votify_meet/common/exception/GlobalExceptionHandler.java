package com.example.votify_meet.common.exception;

import com.example.votify_meet.auth.domain.exception.TokenExpiredException;
import com.example.votify_meet.auth.domain.exception.TokenNotFoundException;
import com.example.votify_meet.auth.domain.exception.TokenRevokedException;
import com.example.votify_meet.events.domain.exception.EventNotFoundException;
import com.example.votify_meet.options.domain.exception.OptionNotFoundException;
import com.example.votify_meet.options.domain.exception.UnauthorizedException;
import com.example.votify_meet.users.domain.exception.UserIsAlreadyExistsException;
import com.example.votify_meet.users.domain.exception.UserNotFoundException;
import com.example.votify_meet.votes.domain.exception.AlreadyVotedException;
import com.example.votify_meet.votes.domain.exception.VoteNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.ResponseEntity.internalServerError;


@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Common error login
    private void logException(Exception ex, HttpStatus status, String message) {
        String currentUser = "anonymousUser";
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            currentUser = auth.getName();
        }
        MDC.put("user", currentUser);
        MDC.put("exception_type", ex.getClass().getSimpleName());
        MDC.put("status_code", String.valueOf(status.value()));

        if (status.is5xxServerError()) {
            logger.error("SYSTEM_ERROR: {}", message, ex); // with StackTree
        } else {
            logger.warn(message);
        }
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Object handleAll(Exception ex, WebRequest request) {
        logException(ex, HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        return internalServerError().body("An error occurred.");
    }

    @ExceptionHandler({
            UserNotFoundException.class,
            EventNotFoundException.class,
            OptionNotFoundException.class,
            VoteNotFoundException.class,
            TokenNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleResourceNotFoundExceptions(RuntimeException ex) {
        logException(ex, HttpStatus.NOT_FOUND, ex.getMessage());
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler({
            AlreadyVotedException.class,
            UserIsAlreadyExistsException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleResourceConflictException(
            RuntimeException ex

    ) {
        logException(ex, HttpStatus.CONFLICT, ex.getMessage());
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors()
                .forEach(error ->
                        errors.put(error.getField(), error.getDefaultMessage())
                );

        String cleanMessage = "Validation Error: " + errors.toString();
        logException(ex, HttpStatus.BAD_REQUEST, cleanMessage);
        return errors;
    }

    @ExceptionHandler({
            UnauthorizedException.class,
            TokenExpiredException.class,
            TokenRevokedException.class
    })
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, String> handleUnauthorizedException(
            RuntimeException ex) {
        logException(ex, HttpStatus.UNAUTHORIZED, ex.getMessage());
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(AppAccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> handleAccessDeniedException(
            AppAccessDeniedException ex) {
        logException(ex, HttpStatus.FORBIDDEN, ex.getMessage());
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, String> handleBadCredentialsException(BadCredentialsException ex) {
        return Map.of("error", "Invalid email or password");
    }
}

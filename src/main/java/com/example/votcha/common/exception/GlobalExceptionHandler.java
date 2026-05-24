package com.example.votcha.common.exception;

import com.example.votcha.auth.domain.exception.TokenExpiredException;
import com.example.votcha.auth.domain.exception.TokenNotFoundException;
import com.example.votcha.auth.domain.exception.TokenRevokedException;
import com.example.votcha.auth.domain.exception.UserNotVerifiedException;
import com.example.votcha.events.domain.exception.EventDeadlinePassedException;
import com.example.votcha.events.domain.exception.EventNotFoundException;
import com.example.votcha.options.domain.exception.MinimumOptionsException;
import com.example.votcha.options.domain.exception.OptionNotFoundException;
import com.example.votcha.options.domain.exception.UnauthorizedException;
import com.example.votcha.users.domain.exception.AdminActionException;
import com.example.votcha.users.domain.exception.InvalidPasswordException;
import com.example.votcha.users.domain.exception.UserIsAlreadyExistsException;
import com.example.votcha.users.domain.exception.UserNotFoundException;
import com.example.votcha.votes.domain.exception.AlreadyVotedException;
import com.example.votcha.votes.domain.exception.VoteNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import tools.jackson.databind.exc.UnrecognizedPropertyException;

import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
public class    GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);



    @ExceptionHandler({
            UserNotFoundException.class,
            EventNotFoundException.class,
            OptionNotFoundException.class,
            VoteNotFoundException.class,
            TokenNotFoundException.class,
            UserNotVerifiedException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleResourceNotFoundExceptions(BaseException ex,WebRequest request) {
        logException(ex, HttpStatus.NOT_FOUND, ex.getMessage());
        return buildErrorResponse(ex,HttpStatus.NOT_FOUND,request);
    }

    @ExceptionHandler({
            AlreadyVotedException.class,
            UserIsAlreadyExistsException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleResourceConflictException(
            BaseException ex, WebRequest request
    ) {
        logException(ex, HttpStatus.CONFLICT, ex.getMessage());
        return buildErrorResponse(ex,HttpStatus.CONFLICT,request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(
            MethodArgumentNotValidException ex ,  WebRequest request) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors()
                .forEach(error ->
                        errors.put(error.getField(), error.getDefaultMessage())
                );

        String cleanMessage = "Validation Error: " + errors;
        logException(ex, HttpStatus.BAD_REQUEST, cleanMessage);
        return new ErrorResponse(
                java.time.LocalDateTime.now().toString(),
                HttpStatus.BAD_REQUEST.value(),
                "VALIDATION_ERROR",
                cleanMessage,
                request.getDescription(false).replace("uri=", "")
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse httpMessageNotReadableException(
            HttpMessageNotReadableException ex ,  WebRequest request) {


        String cleanMessage = "Invalid request format";
        String errorCode = "JSON_PARSE_ERROR";
        if(ex.getCause() instanceof UnrecognizedPropertyException unrecognizedPropertyException){
            String unknownField = unrecognizedPropertyException.getPropertyName();
            cleanMessage += " : " + unknownField;
            errorCode = "UNKNOWN_FIELD_ERROR";
        }
        logException(ex, HttpStatus.BAD_REQUEST, cleanMessage);
        return new ErrorResponse(
                java.time.LocalDateTime.now().toString(),
                HttpStatus.BAD_REQUEST.value(),
                errorCode,
                cleanMessage,
                request.getDescription(false).replace("uri=", "")
        );
    }

    @ExceptionHandler({
            InvalidPasswordException.class,
            EventDeadlinePassedException.class,
            MinimumOptionsException.class,
            AdminActionException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(BaseException ex, WebRequest request) {
        String cleanMessage = ex.getMessage();
        logException(ex, HttpStatus.BAD_REQUEST, cleanMessage);

        return buildErrorResponse(ex,HttpStatus.BAD_REQUEST,request);
    }

    @ExceptionHandler({
            UnauthorizedException.class,
            TokenExpiredException.class,
            TokenRevokedException.class
    })
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleUnauthorizedException(
            BaseException ex, WebRequest request) {
        logException(ex, HttpStatus.UNAUTHORIZED, ex.getMessage());
        return buildErrorResponse(ex,HttpStatus.UNAUTHORIZED,request);
    }
    @ExceptionHandler(org.springframework.security.authorization.AuthorizationDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleAuthorizationDeniedException(org.springframework.security.authorization.AuthorizationDeniedException ex, WebRequest request) {
        String cleanMessage = "You do not have permission to perform this action";
        logException(ex, HttpStatus.FORBIDDEN, cleanMessage);

        return new ErrorResponse(
                java.time.LocalDateTime.now().toString(),
                HttpStatus.FORBIDDEN.value(),
                "ACCESS_DENIED",
                cleanMessage,
                request.getDescription(false).replace("uri=", "")
        );
    }

    @ExceptionHandler(AppAccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleAccessDeniedException(
            AppAccessDeniedException ex, WebRequest request) {
        logException(ex, HttpStatus.FORBIDDEN, ex.getMessage());
        return buildErrorResponse(ex,HttpStatus.FORBIDDEN,request);
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleBadCredentialsException(BadCredentialsException ex,WebRequest request) {
        return new ErrorResponse(
                java.time.LocalDateTime.now().toString(),
                HttpStatus.UNAUTHORIZED.value(),
                "INVALID_CREDENTIALS",
                "Invalid email or password",
                request.getDescription(false).replace("uri=", "")
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleAll(Exception ex, WebRequest request) {
        logException(ex, HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        return new ErrorResponse(
                java.time.LocalDateTime.now().toString(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_SERVER_ERROR",
                "An error occurred",
                request.getDescription(false).replace("uri=", "")
        );
    }

    // Common error login
    private void logException(Exception ex, HttpStatus status, String message) {
        try {
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
        }finally {
            MDC.clear();
        }
    }

    private ErrorResponse buildErrorResponse(
            Exception exception,
            HttpStatus status,
            WebRequest request
    ) {
        String path = request.getDescription(false).replace("uri=", "");

        String errorCode = "GENERIC_ERROR";

        if (exception instanceof BaseException baseException) {
            errorCode = baseException.getCode();
        }

        return new ErrorResponse(
                java.time.LocalDateTime.now().toString(),
                status.value(),
                errorCode,
                exception.getMessage(),
                path
        );
    }
}

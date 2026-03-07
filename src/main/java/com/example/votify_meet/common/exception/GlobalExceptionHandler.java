package com.example.votify_meet.common.exception;

import com.example.votify_meet.events.domain.exception.EventNotFoundException;
import com.example.votify_meet.options.domain.exception.OptionNotFoundException;
import com.example.votify_meet.options.domain.exception.UnauthorizedException;
import com.example.votify_meet.users.domain.exception.UserIsAlreadyExistsException;
import com.example.votify_meet.users.domain.exception.UserNotFoundException;
import com.example.votify_meet.votes.domain.exception.AlreadyVotedException;
import com.example.votify_meet.votes.domain.exception.VoteNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;



/*
1. DRY (Don't Repeat Yourself) Prensibi ile Boilerplate'i Azaltmak

Şu anki kalabalık yapı yerine:


@ExceptionHandler({
    UserNotFoundException.class,
    EventNotFoundException.class,
    OptionNotFoundException.class,
    VoteNotFoundException.class
})
@ResponseStatus(HttpStatus.NOT_FOUND)
public Map<String, String> handleResourceNotFoundExceptions(RuntimeException ex) {
    return Map.of("error", ex.getMessage());
}

💡 Daha da iyisi: Gelecekte bu exception'ların hepsini ResourceNotFoundException isimli ortak bir ata (parent) sınıftan miras (extends) aldırırsan, buraya sadece o ata sınıfı yazman yeterli olur.
2. Standardize Edilmiş Hata Objesi ( TODO Kısmın)

Şu an geriye Map<String, String> dönüyorsun. Bu çalışır ama frontend geliştiricisi (veya Flutter uygulaman) için biraz zordur. Çünkü:

    Doğrulama (Validation) hatalarında JSON şu şekilde dönüyor: {"email": "Invalid email", "password": "Too short"}

    Normal hatalarda şöyle dönüyor: {"error": "User not found"}

Frontend'in "Şu an bana alan hatası mı geldi, yoksa genel bir hata mı?" diye if-else yazmasını engellemek için, Java 21'in gücünü kullanıp standart bir Record oluşturmak sektörün altın kuralıdır (RFC 7807 standardı).

Örnek Standart Error Response Modeli:
Java

public record GenericErrorResponse(
        LocalDateTime timestamp,
        int status,
        String message,
        Map<String, String> validationErrors // Sadece @Valid hatalarında dolar, yoksa null olur
) {}

Bunu kullandığında GlobalExceptionHandler sınıfındaki metotların şu kadar şık olur:


@ExceptionHandler(UserIsAlreadyExistsException.class)
@ResponseStatus(HttpStatus.CONFLICT)
public GenericErrorResponse handleUserIsAlreadyExistsException(UserIsAlreadyExistsException ex) {
    return new GenericErrorResponse(
            LocalDateTime.now(),
            HttpStatus.CONFLICT.value(),
            ex.getMessage(),
            null
    );
}

* */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // TODO - We will use generic types in ExceptionHandlers, we'll have a common exception methods and required generic specific methods calls

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors()
                .forEach(error ->
                        errors.put(error.getField(), error.getDefaultMessage())
                );

        return errors;
    }


    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleUserNotFound(
            UserNotFoundException ex) {

        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(EventNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleEventNotFound(
            EventNotFoundException ex) {

        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(OptionNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleOptionNotFound(
            OptionNotFoundException ex) {

        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(VoteNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleVoteNotFound(
            VoteNotFoundException ex) {

        return Map.of("error", ex.getMessage());
    }
    @ExceptionHandler(AlreadyVotedException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleAlreadyVotedException(
            AlreadyVotedException ex) {

        return Map.of("error", ex.getMessage());
    }
    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, String> handleUnauthorizedException(
            UnauthorizedException ex) {

        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(UserIsAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleUserIsAlreadyExistsException(
            UserIsAlreadyExistsException ex) {

        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> handleAccessDeniedException(
            AccessDeniedException ex) {

        return Map.of("error", ex.getMessage());
    }
}

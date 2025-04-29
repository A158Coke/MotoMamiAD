package com.motomami.errorhandling;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.stream.Stream;

@ControllerAdvice
public class ExceptionHandler extends ResponseEntityExceptionHandler {
    private static final String MESSAGE = " message: ";

    public static List<CustomizedErrorMessage> convertToErrorMessageList(CustomizedErrorMessage... errorMessages) {
        return Stream.of(errorMessages).toList();
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(
            final ConstraintViolationException e, final WebRequest request) {
        final String message = e.getMessage();
        final CustomizedErrorMessage errorMessage = new CustomizedErrorMessage(message, ErrorCode.CONTAINS_INVALID_CHARACTERS);
        return this.handleExceptionInternal(e, convertToErrorMessageList(errorMessage),
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }


    @Nullable
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(@NonNull final Exception e, final @Nullable Object body,
                                                             @NonNull final HttpHeaders headers, @NonNull final HttpStatusCode statusCode,
                                                             @NonNull final WebRequest request) {
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(statusCode)) {
            request.setAttribute("jakarta.servlet.error.exception", e, 0);
        }
        return new ResponseEntity<>(body, headers, statusCode);
    }

}

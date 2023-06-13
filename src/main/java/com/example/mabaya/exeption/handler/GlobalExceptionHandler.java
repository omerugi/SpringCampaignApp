package com.example.mabaya.exeption.handler;

import com.example.mabaya.exeption.AppException;
import com.example.mabaya.exeption.AppValidationException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.ZonedDateTime;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppValidationException.class)
    public ResponseEntity<Object> handleAppValidationException(AppValidationException e) {
        AppException appException = new AppException(
                e.getMessage(),
                HttpStatus.BAD_REQUEST,
                ZonedDateTime.now()
        );
        return new ResponseEntity<>(appException, appException.getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentExceptions(MethodArgumentNotValidException e) {
        AppException appException = new AppException(
                HttpStatus.BAD_REQUEST,
                ZonedDateTime.now()
        );
        for (FieldError err : e.getFieldErrors())
            appException.addFieldError(err.getObjectName(), err.getField(), err.getDefaultMessage());
        return new ResponseEntity<>(appException, appException.getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        AppException appException = new AppException(
                e.getMessage(),
                HttpStatus.BAD_REQUEST,
                ZonedDateTime.now()
        );
        return new ResponseEntity<>(appException, appException.getHttpStatus());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRunTimeException(RuntimeException e) {
        AppException appException = new AppException(
                e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                ZonedDateTime.now()
        );
        return new ResponseEntity<>(appException, appException.getHttpStatus());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        AppException appException = new AppException(
                e.getMessage(),
                HttpStatus.BAD_REQUEST,
                ZonedDateTime.now()
        );
        return new ResponseEntity<>(appException, appException.getHttpStatus());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(RuntimeException e) {
        AppException appException = new AppException(
                e.getMessage(),
                HttpStatus.UNPROCESSABLE_ENTITY,
                ZonedDateTime.now()
        );
        return new ResponseEntity<>(appException, appException.getHttpStatus());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException e){
        AppException appException = new AppException(
                e.getCause().getCause().getMessage(),
                HttpStatus.UNPROCESSABLE_ENTITY,
                ZonedDateTime.now()
        );
        return new ResponseEntity<>(appException, appException.getHttpStatus());
    }
}

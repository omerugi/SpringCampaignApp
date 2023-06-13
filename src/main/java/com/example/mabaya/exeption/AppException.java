package com.example.mabaya.exeption;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class AppException {
    private String message;
    private final HttpStatus httpStatus;
    private final ZonedDateTime timeStamp;
    private List<FieldError> fieldErrors = new ArrayList<>();

    public AppException(String message, HttpStatus httpStatus, ZonedDateTime timeStamp) {
        this.message = message;
        this.httpStatus = httpStatus;
        this.timeStamp = timeStamp;
    }

    public AppException(HttpStatus httpStatus, ZonedDateTime timeStamp) {
        this.httpStatus = httpStatus;
        this.timeStamp = timeStamp;
    }

    public void addFieldError(String objectName, String field, String defaultMessage) {
        FieldError error = new FieldError(objectName, field,defaultMessage);
        fieldErrors.add(error);
    }

}

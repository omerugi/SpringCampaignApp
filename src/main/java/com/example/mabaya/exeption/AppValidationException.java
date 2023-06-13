package com.example.mabaya.exeption;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppValidationException extends RuntimeException {

    public AppValidationException(String message) {
        super(message);
    }

    public AppValidationException(String message, Throwable cause){
        super(message, cause);
    }

    @Override
    public String toString() {
        return "ApiValidationException{" +
                "message='" + super.getMessage() + '\'' +
                "} ";
    }
}

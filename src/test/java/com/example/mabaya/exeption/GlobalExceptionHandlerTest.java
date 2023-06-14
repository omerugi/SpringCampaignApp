package com.example.mabaya.exeption;

import static org.mockito.Mockito.when;

import com.example.mabaya.exeption.handler.GlobalExceptionHandler;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.FieldError;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private AppValidationException appValidationException;

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @Mock
    private MethodArgumentTypeMismatchException methodArgumentTypeMismatchException;

    @Mock
    private RuntimeException runtimeException;

    @Mock
    private HttpMessageNotReadableException httpMessageNotReadableException;

    @Mock
    private ConstraintViolationException constraintViolationException;

    @Mock
    private DataIntegrityViolationException dataIntegrityViolationException;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void TestHandleAppValidationException() {
        String exceptionMessage = "App validation exception message";
        when(appValidationException.getMessage()).thenReturn(exceptionMessage);
        ResponseEntity<Object> result = globalExceptionHandler.handleAppValidationException(appValidationException);
        AppException appException = (AppException) result.getBody();
        assertNotNull(appException);
        assertEquals(exceptionMessage, appException.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    void TestHandleMethodArgumentExceptions() {
        String exceptionMessage = "Method argument not valid exception message";
        List<FieldError> errorList = new ArrayList<>( );
        errorList.add(new FieldError("obj","name",exceptionMessage));
        when(methodArgumentNotValidException.getFieldErrors()).thenReturn(errorList);
        ResponseEntity<Object> result = globalExceptionHandler.handleMethodArgumentExceptions(methodArgumentNotValidException);
        AppException appException = (AppException) result.getBody();
        assertNotNull(appException);
        assertFalse(appException.getFieldErrors().isEmpty());
        assertEquals(exceptionMessage, appException.getFieldErrors().get(0).getDefaultMessage());
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    void TestHandleMethodArgumentTypeMismatchException() {
        String exceptionMessage = "Method argument type mismatch exception message";
        when(methodArgumentTypeMismatchException.getMessage()).thenReturn(exceptionMessage);
        ResponseEntity<Object> result = globalExceptionHandler.handleMethodArgumentTypeMismatchException(methodArgumentTypeMismatchException);
        AppException appException = (AppException) result.getBody();
        assertNotNull(appException);
        assertEquals(exceptionMessage, appException.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    void TestHandleRunTimeException() {
        String exceptionMessage = "Runtime exception message";
        when(runtimeException.getMessage()).thenReturn(exceptionMessage);
        ResponseEntity<Object> result = globalExceptionHandler.handleRunTimeException(runtimeException);
        AppException appException = (AppException) result.getBody();
        assertNotNull(appException);
        assertEquals(exceptionMessage, appException.getMessage());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
    }

    @Test
    void TestHandleHttpMessageNotReadableException() {
        String exceptionMessage = "Http message not readable exception message";
        when(httpMessageNotReadableException.getMessage()).thenReturn(exceptionMessage);
        ResponseEntity<Object> result = globalExceptionHandler.handleHttpMessageNotReadableException(httpMessageNotReadableException);
        AppException appException = (AppException) result.getBody();
        assertNotNull(appException);
        assertEquals(exceptionMessage, appException.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    void TestHandleConstraintViolationException() {
        String exceptionMessage = "Constraint violation exception message";
        when(constraintViolationException.getMessage()).thenReturn(exceptionMessage);
        ResponseEntity<Object> result = globalExceptionHandler.handleConstraintViolationException(constraintViolationException);
        AppException appException = (AppException) result.getBody();
        assertNotNull(appException);
        assertEquals(exceptionMessage, appException.getMessage());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, result.getStatusCode());
    }

    @Test
    void TestHandleDataIntegrityViolationException() {
        Throwable cause = Mockito.mock(Throwable.class);
        Throwable rootCause = Mockito.mock(Throwable.class);
        String exceptionMessage = "Data integrity violation exception message";
        when(dataIntegrityViolationException.getCause()).thenReturn(cause);
        when(cause.getCause()).thenReturn(rootCause);
        when(rootCause.getMessage()).thenReturn(exceptionMessage);
        ResponseEntity<Object> result = globalExceptionHandler.handleDataIntegrityViolationException(dataIntegrityViolationException);
        AppException appException = (AppException) result.getBody();
        assertNotNull(appException);
        assertEquals(exceptionMessage, appException.getMessage());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, result.getStatusCode());
    }
}

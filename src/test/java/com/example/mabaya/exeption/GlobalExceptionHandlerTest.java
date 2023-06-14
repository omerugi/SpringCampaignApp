package com.example.mabaya.exeption;

import com.example.mabaya.exeption.handler.GlobalExceptionHandler;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.MethodParameter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@RunWith(SpringRunner.class)
@SpringBootTest
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    void TestHandleAppValidationException() {
        AppValidationException exception = new AppValidationException("Validation exception");
        ResponseEntity<Object> response = globalExceptionHandler.handleAppValidationException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof AppException);
        assertEquals("Validation exception", ((AppException) response.getBody()).getMessage());
    }

    @Test
    void TestHandleMethodArgumentExceptions() {
        MethodParameter methodParameter = mock(MethodParameter.class);
        BindingResult bindingResult = mock(BindingResult.class);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(methodParameter,bindingResult);
        ResponseEntity<Object> response = globalExceptionHandler.handleMethodArgumentExceptions(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof AppException);
    }

    @Test
    void TestHandleRunTimeException() {
        RuntimeException exception = new RuntimeException("Runtime exception");
        ResponseEntity<Object> response = globalExceptionHandler.handleRunTimeException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof AppException);
        assertEquals("Runtime exception", ((AppException) response.getBody()).getMessage());
    }

    @Test
    void TestHandleConstraintViolationException() {
        ConstraintViolationException exception = new ConstraintViolationException("Constraint violation exception", new HashSet<>());
        ResponseEntity<Object> response = globalExceptionHandler.handleConstraintViolationException(exception);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody() instanceof AppException);
        assertEquals("Constraint violation exception", ((AppException) response.getBody()).getMessage());
    }

    @Test
    void TestHandleDataIntegrityViolationException() {
        RuntimeException cause = new RuntimeException(new RuntimeException("Data integrity violation exception"));
        DataIntegrityViolationException exception = new DataIntegrityViolationException("Top level exception", cause);
        ResponseEntity<Object> response = globalExceptionHandler.handleDataIntegrityViolationException(exception);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertTrue(response.getBody() instanceof AppException);
        assertEquals("Data integrity violation exception", ((AppException) response.getBody()).getMessage());
    }

    @Test
    void TestHandleMethodArgumentTypeMismatchException() {
        // TODO: need to create this test
    }

    @Test
    void TestHandleHttpMessageNotReadableException() {
        // TODO: need to create this test
    }

}


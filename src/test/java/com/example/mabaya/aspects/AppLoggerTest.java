package com.example.mabaya.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class AppLoggerTest {

    @Mock
    private ProceedingJoinPoint proceedingJoinPoint;

    @Mock
    private Signature signature;

    @Mock
    private Logger logger;

    private AppLogger appLogger;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        appLogger = new AppLogger();
        ReflectionTestUtils.setField(appLogger, "log", logger);
        when(proceedingJoinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("methodName");
    }

    @Test
    void logBeforeControllersTest() throws Throwable {
        when(proceedingJoinPoint.proceed()).thenReturn(new Object());
        when(proceedingJoinPoint.getArgs()).thenReturn(null);

        appLogger.logAroundControllers(proceedingJoinPoint);

        verify(logger).info("Entering method: {} {}", "methodName", null);
        verify(logger).info("Exiting method: {}", "methodName");
    }

    @Test
    void logExceptionsTest() {
        appLogger.logExceptions(proceedingJoinPoint);

        verify(logger).error("An exception was thrown: ", (Object[]) null);
    }

    //TODO: Add scheduler log

}

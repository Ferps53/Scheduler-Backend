package com.scheduler.core.auth.controller;

import com.scheduler.core.auth.model.ConfirmationCode;
import com.scheduler.core.auth.model.User;
import com.scheduler.core.auth.repository.ConfirmationCodeRepository;
import com.scheduler.core.exceptions.exception.BadRequestException;
import com.scheduler.core.exceptions.exception.NotFoundException;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@QuarkusTest
class ConfirmationCodeControllerTest {

    private static final User TEST_USER = new User("test", "test@gmail.com", "");
    private static final int CODE_SIZE = 6;

    private static final ConfirmationCode CONFIRMATION_CODE = new ConfirmationCode("123456", TEST_USER);
    private static final ConfirmationCode EXPIRED_CODE = new ConfirmationCode("123456", TEST_USER);

    @Inject
    ConfirmationCodeController confirmationCodeController;

    @InjectMock
    ConfirmationCodeRepository repository;

    @BeforeAll
    static void setup() {
        TEST_USER.id = 1L;
        CONFIRMATION_CODE.expiryDate = LocalDateTime.now().plusMinutes(1);
        EXPIRED_CODE.expiryDate = LocalDateTime.now().minusMinutes(1);
    }

    @Test
    void generateConfirmationCode() {

        doNothing().when(repository).persist(any(ConfirmationCode.class));
        when(repository.listAll()).thenReturn(List.of());

        final String code = confirmationCodeController.createCode(CODE_SIZE, TEST_USER);
        assertNotNull(code);
    }

    @Test
    void generateHasDuplicateConfirmationCode() {

        doNothing().when(repository).persist(any(ConfirmationCode.class));
        when(repository.listAll()).thenReturn(List.of(CONFIRMATION_CODE));


        final String code = confirmationCodeController.createCode(CODE_SIZE, TEST_USER);
        assertNotNull(code);
    }

    @Test
    void validateCode() {

        doNothing().when(repository).delete(any(ConfirmationCode.class));
        when(repository.findConfirmationCodeByCodeAndUserEmail(anyString(), anyString()))
                .thenReturn(CONFIRMATION_CODE);
        assertDoesNotThrow(() -> confirmationCodeController.validateCode("123456", "test@gmail.com"));
    }

    @Test
    void validateExpiredCode() {

        when(repository.findConfirmationCodeByCodeAndUserEmail(anyString(), anyString()))
                .thenReturn(EXPIRED_CODE);
        assertThrows(BadRequestException.class, () -> confirmationCodeController.validateCode("123456", "test@gmail.com"));
    }

    @Test
    void validateInvalidCode() {

        when(repository.findConfirmationCodeByCodeAndUserEmail(anyString(), anyString()))
                .thenReturn(null);
        assertThrows(NotFoundException.class, () -> confirmationCodeController.validateCode("123456", "test@gmail.com"));
    }
}

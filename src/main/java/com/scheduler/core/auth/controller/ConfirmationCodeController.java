package com.scheduler.core.auth.controller;

import com.scheduler.core.auth.model.ConfirmationCode;
import com.scheduler.core.auth.model.User;
import com.scheduler.core.auth.repository.ConfirmationCodeRepository;
import com.scheduler.core.exceptions.exception.BadRequestException;
import com.scheduler.core.exceptions.exception.NotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.util.Random;

@ApplicationScoped
public class ConfirmationCodeController {

    @Inject
    ConfirmationCodeRepository repository;

    private final Random random = new Random();

    public String createCode(int size, User user) {

        String code = generateCode(size);
        final var confirmationCodes = repository.listAll();

        for (ConfirmationCode confirmationCode : confirmationCodes) {

            if (confirmationCode.code.equals(code)) {
                code = generateCode(size);
            }
        }

        final var confirmationCode = new ConfirmationCode(code, user);

        repository.persist(confirmationCode);
        return code;
    }

    public void validateCode(String code, String email) {

        final ConfirmationCode confirmationCode = repository.findConfirmationCodeByCodeAndUserEmail(code, email);

        if (confirmationCode == null)
            throw new NotFoundException("Invalid confirmation code");

        if (confirmationCode.expiryDate.isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Expired confirmation code");
        }

        // Deletes the code so it isn't used twice
        repository.delete(confirmationCode);
    }

    private String generateCode(int size) {
        final StringBuilder randomCode = new StringBuilder();
        for (int i = 0; i < size; i++) {
            randomCode.append(random.nextInt(10));
        }

        return randomCode.toString();
    }
}
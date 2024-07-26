package com.scheduler.core.auth.controller;

import com.scheduler.core.auth.model.ConfirmationCode;
import com.scheduler.core.auth.model.User;
import com.scheduler.core.exceptions.exception.BadRequestException;
import com.scheduler.core.exceptions.exception.NotFoundException;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@ApplicationScoped
public class ConfirmationCodeController {

    private static final Random RANDOM = new Random();

    public String createCode(int size, User user) {

        String code = generateCode(size);
        List<ConfirmationCode> confirmationCodes = ConfirmationCode.listAll();

        for (ConfirmationCode confirmationCode : confirmationCodes) {

            if (confirmationCode.code.equals(code)) {
                code = generateCode(size);
            }
        }

        new ConfirmationCode(code, user).persist();
        return code;
    }

    public void validateCode(String code, String email) {

        final ConfirmationCode confirmationCode = ConfirmationCode.find("code = ?1 and user.email = ?2", code, email).firstResult();

        if (confirmationCode == null)
            throw new NotFoundException("Invalid confirmation code");

        if (confirmationCode.expiryDate.isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Expired confirmation code");
        }

        // Deletes the code so it isn't used twice
        confirmationCode.delete();
    }

    private String generateCode(int size) {

        StringBuilder randomCode = new StringBuilder();
        for (int i = 0; i < size; i++) {
            randomCode.append(RANDOM.nextInt(10));
        }

        return randomCode.toString();
    }
}
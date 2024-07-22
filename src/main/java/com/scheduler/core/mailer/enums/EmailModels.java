package com.scheduler.core.mailer.enums;

public enum EmailModels {

    EMAIL_CONFIRMATION(
            "email/confirm_email.html",
            "Confirme seu email"
    );

    final public String templateAddress;

    final public String subject;

    EmailModels(String templateAddress, String subject) {
        this.templateAddress = templateAddress;
        this.subject = subject;
    }
}

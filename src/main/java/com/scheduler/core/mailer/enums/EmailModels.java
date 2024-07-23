package com.scheduler.core.mailer.enums;

public enum EmailModels {

    EMAIL_CONFIRMATION(
            "/email/confirm_email.html",
            "Confirme seu email"
    );

    private final String templateAddress;

    private final String subject;

    EmailModels(String templateAddress, String subject) {
        this.templateAddress = templateAddress;
        this.subject = subject;
    }

    public String getTemplateAddress() {
        return templateAddress;
    }

    public String getSubject() {
        return subject;
    }
}

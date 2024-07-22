package com.scheduler.core.mailer.controller;

import com.scheduler.core.exceptions.exception.BadRequestException;
import com.scheduler.core.mailer.dto.EmailContentsDTO;
import com.scheduler.core.mailer.dto.EmailDTO;
import com.scheduler.core.mailer.enums.EmailImages;
import com.scheduler.core.mailer.enums.EmailModels;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.inject.Inject;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EmailController {

    @Inject
    Mailer mailer;

    public void sendEmail(EmailDTO email) {
        Thread thread = new Thread(() -> {
            String html = loadTemplate(email.model());
            html = replaceContents(email.contents(), html);

            Mail mail = Mail.withHtml(email.address(), email.model().subject, html);
            loadAssets(email.images(), mail);

            mailer.send(mail);
        });

        thread.start();
    }

    private String loadTemplate(EmailModels model) {

        try (InputStream inputStream = getClass().getResourceAsStream(model.templateAddress)) {

            if (inputStream == null) {
                throw new BadRequestException("Template not found");
            }

            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new BadRequestException(e);
        }
    }

    private String replaceContents(List<EmailContentsDTO> emailContents, String html) {

        for (EmailContentsDTO contents : emailContents) {
            if (!contents.isImage()) {
                html = html.replace(contents.key(), contents.value());
            }
        }
        return html;
    }

    private void loadAssets(List<EmailImages> images, Mail mail) {

        for (EmailImages image : images) {

            final URL imageUrl = getClass().getResource(image.getImageAddress());
            if (imageUrl == null) throw new BadRequestException();

            File imageFile = new File(imageUrl.getFile());

            mail.addInlineAttachment(image.getName(), imageFile, image.getType(), image.getCid());
        }
    }
}

package exceptions;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

@ApplicationScoped
public final class MessageTranslator {

    private MessageTranslator() {
    }

    public static String translate(String messageCode) {

        try {
            return ResourceBundle.getBundle(
                            "messages",
                            Locale.of("pt", "BR"
                            )
                    )
                    .getString(messageCode);
        } catch (MissingResourceException e) {
            return messageCode;
        }
    }
}

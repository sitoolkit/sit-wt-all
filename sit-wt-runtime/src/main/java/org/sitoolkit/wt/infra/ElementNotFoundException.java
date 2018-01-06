package org.sitoolkit.wt.infra;

import org.sitoolkit.wt.domain.testscript.Locator;
import org.sitoolkit.wt.infra.resource.MessageManager;

public class ElementNotFoundException extends RuntimeException {

    public ElementNotFoundException() {
        super();
    }

    public ElementNotFoundException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ElementNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ElementNotFoundException(String message) {
        super(message);
    }

    public ElementNotFoundException(Throwable cause) {
        super(cause);
    }

    public static ElementNotFoundException create(Locator locator, Throwable cause) {
        String message = buildMessage(locator);
        return new ElementNotFoundException(message, cause);
    }

    private static String buildMessage(Locator locator) {
        return MessageManager.getMessage("build.message", locator);
    }
}

package ch.pfaditools.accounting.logger;

import java.util.logging.Level;
import java.util.logging.Logger;

public interface HasLogger {

    default Logger getLogger() {
        return Logger.getLogger(this.getClass().getName());
    }

    default void logSevere(String message) {
        getLogger().log(Level.SEVERE, message);
    }

    default void logSevere(String message, Throwable throwable) {
        getLogger().log(Level.SEVERE, message, throwable);
    }

    default void logWarning(String message, Throwable throwable) {
        getLogger().log(Level.WARNING, message, throwable);
    }

    default void logWarning(String message) {
        getLogger().log(Level.WARNING, message);
    }

    default void logInfo(String message, Throwable throwable) {
        getLogger().log(Level.INFO, message, throwable);
    }

    default void logInfo(String message) {
        getLogger().log(Level.INFO, message);
    }

    default void logDebug(String message, Throwable throwable) {
        getLogger().log(Level.FINE, message, throwable);
    }

    default void logDebug(String message) {
        getLogger().log(Level.FINE, message);
    }
}

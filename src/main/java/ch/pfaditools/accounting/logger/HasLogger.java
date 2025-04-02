package ch.pfaditools.accounting.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface HasLogger {

    default Logger getLogger() {
        return LoggerFactory.getLogger(this.getClass());
    }
}

package draylar.identity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Core entry point for shared Identity logic.
 */
public final class Identity {
    public static final String MODID = "identity";
    public static final Logger LOGGER = LoggerFactory.getLogger(Identity.class);

    private Identity() {
    }

    public static void init() {
        LOGGER.info("Initializing Identity core module");
    }
}


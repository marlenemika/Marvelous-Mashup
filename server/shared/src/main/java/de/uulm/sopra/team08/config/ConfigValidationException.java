package de.uulm.sopra.team08.config;

/**
 * Thrown to indicate an invalid config
 */
public class ConfigValidationException extends Exception {

    ConfigValidationException(String msg) {
        super(msg);
    }

}

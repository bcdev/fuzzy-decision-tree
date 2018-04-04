package com.bc.dectree;

/**
 * Exception type thrown while interpreting decision tree YAML files.
 */
public class DecTreeParseException extends Exception {
    public DecTreeParseException(String message) {
        super(message);
    }

    public DecTreeParseException(String message, Throwable cause) {
        super(message, cause);
    }
}

package com.practice.etc.error;

/**
 * User: tomer
 */
public class LogicalException extends RuntimeException {
    private String nlsKey;

    public LogicalException() {
        super();
    }

    public LogicalException(String message, Throwable cause) {
        super(message, cause);
    }

    public LogicalException(String message) {
        super(message);
    }

    public LogicalException(String message, String nlsKey) {
        super(message);
        this.nlsKey = nlsKey;
    }

    public LogicalException(Throwable cause) {
        super(cause);
    }

    public String getNlsKey() {
        return nlsKey;
    }

    @Override
    public String getMessage() {
        return super.getMessage() == null ? "" : super.getMessage();
    }
}
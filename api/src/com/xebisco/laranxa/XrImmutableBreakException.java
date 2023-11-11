package com.xebisco.laranxa;

public class XrImmutableBreakException extends RuntimeException {
    public XrImmutableBreakException() {
        super();
    }

    public XrImmutableBreakException(String message) {
        super(message);
    }

    public XrImmutableBreakException(String message, Throwable cause) {
        super(message, cause);
    }

    public XrImmutableBreakException(Throwable cause) {
        super(cause);
    }

    protected XrImmutableBreakException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

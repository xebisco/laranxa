package com.xebisco.laranxa.opengl;

public class GLFWException extends RuntimeException {
    public GLFWException() {
    }

    public GLFWException(String message) {
        super(message);
    }

    public GLFWException(String message, Throwable cause) {
        super(message, cause);
    }

    public GLFWException(Throwable cause) {
        super(cause);
    }

    public GLFWException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

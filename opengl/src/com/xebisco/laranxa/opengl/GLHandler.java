package com.xebisco.laranxa.opengl;

public class GLHandler {
    protected long handler;

    public GLHandler() {
    }

    public GLHandler(long handler) {
        this.handler = handler;
    }

    public long handler() {
        return handler;
    }
}

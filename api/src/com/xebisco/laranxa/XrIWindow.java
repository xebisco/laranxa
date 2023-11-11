package com.xebisco.laranxa;

public interface XrIWindow extends XrIInit, AutoCloseable, XrIProcess {
    void setup(final XrWindowConfig config);
    boolean shouldClose();
}

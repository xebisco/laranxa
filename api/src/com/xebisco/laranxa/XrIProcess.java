package com.xebisco.laranxa;

public interface XrIProcess {
    Object process(final Object self, final Object value);
    default Object process() {
        return process(this, null);
    }
}

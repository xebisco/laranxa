package com.xebisco.laranxa;

public final class XrGlobal {
    public static final XrIProcess NULL_PROCESS = new NullProcess();

    static class NullProcess implements XrIProcess {
        @Override
        public Object process(Object self, Object value) {
            return value;
        }
    }
}

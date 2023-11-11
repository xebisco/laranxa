package com.xebisco.laranxa;

public final class XrUtils {
    public static float clamp(float value, float min, float max) {
        return Math.max(Math.min(value, max), min);
    }
}

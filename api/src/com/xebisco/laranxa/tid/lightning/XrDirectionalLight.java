package com.xebisco.laranxa.tid.lightning;

import com.xebisco.laranxa.utils.XrColor;
import com.xebisco.laranxa.utils.XrIPoint3;

public final class XrDirectionalLight {
    private final XrColor color;
    private final XrIPoint3 direction;
    private float intensity;

    public XrDirectionalLight(XrColor color, XrIPoint3 direction, float intensity) {
        this.color = color;
        this.direction = direction;
        this.intensity = intensity;
    }

    public XrColor color() {
        return color;
    }

    public XrIPoint3 direction() {
        return direction;
    }

    public float intensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }
}

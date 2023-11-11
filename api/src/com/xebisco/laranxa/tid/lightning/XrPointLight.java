package com.xebisco.laranxa.tid.lightning;

import com.xebisco.laranxa.utils.XrColor;
import com.xebisco.laranxa.utils.XrIPoint3;

public final class XrPointLight {
    private final XrAttenuation attenuation;
    private final XrColor color;
    private float intensity;
    private final XrIPoint3 position;

    public XrPointLight(XrAttenuation attenuation, XrColor color, float intensity, XrIPoint3 position) {
        this.attenuation = attenuation;
        this.color = color;
        this.intensity = intensity;
        this.position = position;
    }

    public XrAttenuation attenuation() {
        return attenuation;
    }

    public XrColor color() {
        return color;
    }

    public float intensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public XrIPoint3 position() {
        return position;
    }
}

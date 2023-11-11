package com.xebisco.laranxa.tid.lightning;

import com.xebisco.laranxa.utils.XrIPoint3;

public class XrSpotLight {
    private final XrIPoint3 coneDirection;
    private float cutOff;
    private float cutOffAngle;
    private XrPointLight pointLight;

    public XrSpotLight(XrIPoint3 coneDirection, float cutOffAngle, XrPointLight pointLight) {
        this.coneDirection = coneDirection;
        setCutOffAngle(cutOffAngle);
        this.pointLight = pointLight;
    }

    public XrIPoint3 coneDirection() {
        return coneDirection;
    }

    public float cutOff() {
        return cutOff;
    }

    public float cutOffAngle() {
        return cutOffAngle;
    }

    public void setCutOffAngle(float cutOffAngle) {
        this.cutOffAngle = cutOffAngle;
        cutOff = (float) Math.cos(Math.toRadians(cutOffAngle));
    }

    public XrPointLight pointLight() {
        return pointLight;
    }

    public void setPointLight(XrPointLight pointLight) {
        this.pointLight = pointLight;
    }
}

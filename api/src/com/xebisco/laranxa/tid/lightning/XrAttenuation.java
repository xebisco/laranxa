package com.xebisco.laranxa.tid.lightning;

public final class XrAttenuation {
    private float constant, exponent, linear;

    public XrAttenuation(float constant, float exponent, float linear) {
        this.constant = constant;
        this.exponent = exponent;
        this.linear = linear;
    }

    public float constant() {
        return constant;
    }

    public void setConstant(float constant) {
        this.constant = constant;
    }

    public float exponent() {
        return exponent;
    }

    public void setExponent(float exponent) {
        this.exponent = exponent;
    }

    public float linear() {
        return linear;
    }

    public void setLinear(float linear) {
        this.linear = linear;
    }
}

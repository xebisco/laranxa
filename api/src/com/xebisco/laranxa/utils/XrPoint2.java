package com.xebisco.laranxa.utils;

public class XrPoint2 implements XrIPoint2 {

    public float x, y;

    public XrPoint2() {

    }

    public XrPoint2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public XrPoint2(XrIPoint2 point) {
        this.x = point.x();
        this.y = point.y();
    }

    @Override
    public float x() {
        return x;
    }

    @Override
    public float y() {
        return y;
    }

    @Override
    public XrIPoint2 add(XrIPoint2 v) {
        x += v.x();
        y += v.y();
        return this;
    }

    @Override
    public XrIPoint2 sub(XrIPoint2 v) {
        x -= v.x();
        y -= v.y();
        return this;
    }

    @Override
    public XrIPoint2 mul(XrIPoint2 v) {
        x *= v.x();
        y *= v.y();
        return this;
    }

    @Override
    public XrIPoint2 div(XrIPoint2 v) {
        x /= v.x();
        y /= v.y();
        return this;
    }

    @Override
    public String toString() {
        return "XrPoint2{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}

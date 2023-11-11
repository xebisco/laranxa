package com.xebisco.laranxa.utils;

public class XrPoint3 extends XrPoint2 implements XrIPoint3 {

    public float z;

    public XrPoint3() {
    }

    public XrPoint3(float x, float y, float z) {
        super(x, y);
        this.z = z;
    }

    public XrPoint3(XrIPoint2 point, float z) {
        super(point);
        this.z = z;
    }

    public XrPoint3(XrIPoint3 point) {
        super(point);
        this.z = point.z();
    }

    @Override
    public float z() {
        return z;
    }

    @Override
    public XrIPoint3 add(XrIPoint3 v) {
        z += v.z();
        return (XrIPoint3) add((XrIPoint2) v);
    }

    @Override
    public XrIPoint3 sub(XrIPoint3 v) {
        z -= v.z();
        return (XrIPoint3) sub((XrIPoint2) v);
    }

    @Override
    public XrIPoint3 mul(XrIPoint3 v) {
        z *= v.z();
        return (XrIPoint3) mul((XrIPoint2) v);
    }

    @Override
    public XrIPoint3 div(XrIPoint3 v) {
        z /= v.z();
        return (XrIPoint3) div((XrIPoint2) v);
    }

    @Override
    public String toString() {
        return "XrPoint3{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}

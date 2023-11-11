package com.xebisco.laranxa.bid;

import com.xebisco.laranxa.utils.XrIPoint2;
import com.xebisco.laranxa.utils.XrPoint2;

public class XrTransformation2D {
    private final XrPoint2 position = new XrPoint2(), scale = new XrPoint2(1, 1);
    private float rotation;

    public XrTransformation2D translate(XrIPoint2 v) {
        position.add(v);
        return this;
    }

    public XrTransformation2D rotate(float v) {
        rotation += v;
        return this;
    }

    public XrPoint2 position() {
        return position;
    }

    public XrPoint2 scale() {
        return scale;
    }

    public float rotation() {
        return rotation;
    }

    public XrTransformation2D setRotation(float rotation) {
        this.rotation = rotation;
        return this;
    }
}

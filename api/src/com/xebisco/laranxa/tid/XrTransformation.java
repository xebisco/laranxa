package com.xebisco.laranxa.tid;

import com.xebisco.laranxa.utils.XrIPoint3;
import com.xebisco.laranxa.utils.XrPoint3;

public class XrTransformation {
    private final XrPoint3 position = new XrPoint3(), scale = new XrPoint3(1, 1, 1), rotation = new XrPoint3();

    public XrTransformation translate(XrIPoint3 v) {
        position.add(v);
        return this;
    }

    public XrTransformation rotate(XrIPoint3 v) {
        rotation.add(v);
        return this;
    }

    public XrPoint3 position() {
        return position;
    }

    public XrPoint3 scale() {
        return scale;
    }

    public XrPoint3 rotation() {
        return rotation;
    }

    @Override
    public String toString() {
        return "XrTransformation{" +
                "position=" + position +
                ", scale=" + scale +
                ", rotation=" + rotation +
                '}';
    }
}

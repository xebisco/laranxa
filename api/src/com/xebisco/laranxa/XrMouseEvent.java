package com.xebisco.laranxa;

import com.xebisco.laranxa.utils.XrIPoint2;

public final class XrMouseEvent {
    private final XrIPoint2 position;
    private final XrMouseButton button;

    public XrMouseEvent(XrIPoint2 position, XrMouseButton button) {
        this.position = position;
        this.button = button;
    }

    public XrIPoint2 position() {
        return position;
    }

    public XrMouseButton button() {
        return button;
    }
}

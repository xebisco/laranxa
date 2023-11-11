package com.xebisco.laranxa.utils;

import com.xebisco.laranxa.XrImmutableBreakException;
import com.xebisco.laranxa.utils.XrColor;

public final class XrImmutableColor extends XrColor {
    public XrImmutableColor(int color) {
        super(color);
    }

    public XrImmutableColor(int color, Format format) {
        super(color, format);
    }

    public XrImmutableColor(XrColor toCopy) {
        super(toCopy);
    }

    public XrImmutableColor(int rgb, float alpha) {
        super(rgb, alpha);
    }

    public XrImmutableColor(float red, float green, float blue) {
        super(red, green, blue);
    }

    public XrImmutableColor(float red, float green, float blue, float alpha) {
        super(red, green, blue, alpha);
    }

    @Override
    public XrColor setRed(float red) {
        throw new XrImmutableBreakException();
    }

    @Override
    public XrColor setGreen(float green) {
        throw new XrImmutableBreakException();
    }

    @Override
    public XrColor setBlue(float blue) {
        throw new XrImmutableBreakException();
    }

    @Override
    public XrColor setAlpha(float alpha) {
        throw new XrImmutableBreakException();
    }
}

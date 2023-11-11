package com.xebisco.laranxa.utils;

import com.xebisco.laranxa.XrGlobal;
import com.xebisco.laranxa.XrIProcess;

public class XrDimension implements XrIDimension {
    private XrIProcess valueProcess = XrGlobal.NULL_PROCESS;
    private float width, height;

    public XrDimension() {
    }

    public XrDimension(final float width, final float height) {
        this.width = width;
        this.height = height;
    }

    public XrDimension(final XrIDimension dimension) {
        this.width = dimension.width();
        this.height = dimension.height();
    }

    @Override
    public float width() {
        return width;
    }

    public XrDimension setWidth(float width) {
        this.width = (float) valueProcess.process(this, width);
        return this;
    }

    @Override
    public float height() {
        return height;
    }

    public XrDimension setHeight(float height) {
        this.height = (float) valueProcess.process(this, height);
        return this;
    }

    public XrIProcess valueProcess() {
        return valueProcess;
    }

    public XrDimension setValueProcess(XrIProcess valueProcess) {
        this.valueProcess = valueProcess;
        return this;
    }
}

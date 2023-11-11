package com.xebisco.laranxa.utils;

import com.xebisco.laranxa.XrIProcess;
import com.xebisco.laranxa.XrImmutableBreakException;
import com.xebisco.laranxa.utils.XrDimension;
import com.xebisco.laranxa.utils.XrIDimension;

public final class XrImmutableDimension extends XrDimension {
    public XrImmutableDimension() {
        setup();
    }

    public XrImmutableDimension(float width, float height) {
        super(width, height);
        setup();
    }

    public XrImmutableDimension(XrIDimension dimension) {
        super(dimension);
        setup();
    }

    private void setup() {
        setValueProcess((self, value) -> {
            throw new XrImmutableBreakException();
        });
    }

    @Override
    public XrDimension setValueProcess(XrIProcess valueProcess) {
        throw new XrImmutableBreakException();
    }
}

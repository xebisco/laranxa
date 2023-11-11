package com.xebisco.laranxa.opengl;

import com.xebisco.laranxa.XrIDefs;
import com.xebisco.laranxa.XrIWindow;

public final class GLDefs implements XrIDefs {
    @Override
    public Class<? extends XrIWindow> implWindow() {
        return GLWindow.class;
    }
}

package com.xebisco.laranxa;

import com.xebisco.laranxa.utils.XrDimension;
import com.xebisco.laranxa.utils.XrIDimension;

public class XrWindowConfig {
    public XrIDimension size = new XrDimension(1280, 720);
    public String title = "Sample Window";
    public XrIKeyListener[] keyListeners = new XrIKeyListener[0];
    public XrIMouseListener[] mouseListeners = new XrIMouseListener[0];
    public boolean resizable = true, vSync, antiAliasing = true;
}

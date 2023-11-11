package com.xebisco.laranxa;

import com.xebisco.laranxa.utils.XrDimension;
import com.xebisco.laranxa.utils.XrIDimension;

public record XrTexture(String path, XrIDimension size, XrTextureFilter filter) {
    public XrTexture(String path, XrTextureFilter filter) {
        this(path, new XrDimension(), filter);
    }
}

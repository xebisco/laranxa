package com.xebisco.laranxa.bid.object;

import com.xebisco.laranxa.XrTexture;
import com.xebisco.laranxa.XrTextureFilter;

public final class XrObject2D {
    private final XrMesh2D mesh2D;
    private XrTexture texture;

    public static final XrTexture DEFAULT_TEXTURE_2D = new XrTexture("com/xebisco/laranxa/logo.png", XrTextureFilter.LINEAR);

    public XrObject2D(XrMesh2D mesh2D, XrTexture texture) {
        this.mesh2D = mesh2D;
        this.texture = texture;
    }

    public XrObject2D(XrMesh2D mesh2D) {
        this(mesh2D, DEFAULT_TEXTURE_2D);
    }

    public XrMesh2D mesh2D() {
        return mesh2D;
    }

    public XrTexture texture() {
        return texture;
    }

    public XrObject2D setTexture(XrTexture texture) {
        this.texture = texture;
        return this;
    }
}

package com.xebisco.laranxa.opengl;

import com.xebisco.laranxa.XrTexture;

import java.util.HashMap;
import java.util.Map;

public final class GLTextureCache {
    private static final Map<XrTexture, GLTexture> textureMap = new HashMap<>();

    public static GLTexture get(XrTexture texture) {
        if (textureMap.containsKey(texture)) return textureMap.get(texture);
        GLTexture glTexture = new GLTexture(texture);
        textureMap.put(texture, glTexture);
        return glTexture;
    }

    public static void clear() {
        textureMap.values().forEach(GLTexture::close);
        textureMap.clear();
    }
}

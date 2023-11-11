package com.xebisco.laranxa.opengl;

import com.xebisco.laranxa.utils.XrDimension;
import com.xebisco.laranxa.XrTexture;
import com.xebisco.laranxa.XrTextureFilter;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;

public class GLTexture implements AutoCloseable {

    private final int handler;
    private final XrTexture tex;
    private final boolean hasTransparency;

    public GLTexture(XrTexture tex) {
        ByteBuffer imageBuffer;
        this.tex = tex;
        try {
            imageBuffer = GLUtils.inputStreamToByteBuffer(GLTextureCache.class.getResourceAsStream("/" + tex.path()), 128 * 128);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        IntBuffer w = BufferUtils.createIntBuffer(1);
        IntBuffer h = BufferUtils.createIntBuffer(1);
        IntBuffer c = BufferUtils.createIntBuffer(1);


        if (!stbi_info_from_memory(imageBuffer, w, h, c)) {
            throw new RuntimeException("Failed to read image information: " + stbi_failure_reason());
        }

        ByteBuffer image = stbi_load_from_memory(imageBuffer, w, h, c, 0);
        if (image == null) {
            throw new RuntimeException("Failed to load image: " + stbi_failure_reason());
        }

        int width = w.get(0);
        int height = h.get(0);
        int comp = c.get(0);

        handler = glGenTextures();

        glBindTexture(GL_TEXTURE_2D, handler);

        if (comp == 3) {
            if ((width & 3) != 0) {
                glPixelStorei(GL_UNPACK_ALIGNMENT, 2 - (width & 1));
            }
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, image);
            hasTransparency = false;
        } else {
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
            hasTransparency = true;
        }

        int filter = tex.filter() == XrTextureFilter.LINEAR ? GL_LINEAR : GL_NEAREST;
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filter);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, filter);


        stbi_image_free(image);

        if (tex.size() instanceof XrDimension) ((XrDimension) tex.size()).setWidth(width).setHeight(height);
    }

    @Override
    public void close() {
        glDeleteTextures(handler);
    }

    public int handler() {
        return handler;
    }

    public XrTexture tex() {
        return tex;
    }

    public boolean hasTransparency() {
        return hasTransparency;
    }
}

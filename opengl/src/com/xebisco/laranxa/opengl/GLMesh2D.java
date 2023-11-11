package com.xebisco.laranxa.opengl;

import com.xebisco.laranxa.bid.XrVertex2D;
import com.xebisco.laranxa.bid.object.XrMesh2D;
import com.xebisco.laranxa.tid.XrVertex;
import com.xebisco.laranxa.tid.animation.XrBone;
import com.xebisco.laranxa.tid.animation.XrVertexWeight;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.lwjgl.opengl.GL30.*;

public class GLMesh2D implements AutoCloseable {
    public final int vertexCount, vao;
    private final List<Integer> vbos = new ArrayList<>();

    public GLMesh2D(XrMesh2D xrMesh) {
        vertexCount = xrMesh.indices().length;

        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        int vbo = glGenBuffers();
        vbos.add(vbo);
        FloatBuffer positionsBuffer = MemoryUtil.memCallocFloat(xrMesh.vertices().length * 2);
        for (XrVertex2D v : xrMesh.vertices()) {
            positionsBuffer.put(v.position().x());
            positionsBuffer.put(v.position().y());
        }
        positionsBuffer.flip();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, positionsBuffer, GL_STATIC_DRAW);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
        MemoryUtil.memFree(positionsBuffer);

        vbo = glGenBuffers();
        vbos.add(vbo);
        FloatBuffer textCoordsBuffer = MemoryUtil.memCallocFloat(xrMesh.vertices().length * 2);
        for (XrVertex2D v : xrMesh.vertices()) {
            textCoordsBuffer.put(v.textureCoord().x());
            textCoordsBuffer.put(v.textureCoord().y());
        }
        textCoordsBuffer.flip();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, textCoordsBuffer, GL_STATIC_DRAW);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
        MemoryUtil.memFree(textCoordsBuffer);


        vbo = glGenBuffers();
        vbos.add(vbo);
        IntBuffer indicesBuffer = MemoryUtil.memCallocInt(vertexCount);
        indicesBuffer.put(xrMesh.indices()).flip();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
        MemoryUtil.memFree(indicesBuffer);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        xrMesh.setVertices(null);
        xrMesh.setIndices(null);
        System.gc();
    }

    @Override
    public void close() {
        glDisableVertexAttribArray(0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        vbos.forEach(GL20::glDeleteBuffers);

        glBindVertexArray(0);
        glDeleteVertexArrays(vao);
    }
}

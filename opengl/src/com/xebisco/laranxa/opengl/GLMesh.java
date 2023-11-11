package com.xebisco.laranxa.opengl;

import com.xebisco.laranxa.tid.object.XrMesh;
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

public class GLMesh implements AutoCloseable {
    public final int vertexCount, vao;
    private final List<Integer> vbos = new ArrayList<>();

    public GLMesh(XrMesh xrMesh) {
        vertexCount = xrMesh.faces().length;

        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        int vbo = glGenBuffers();
        vbos.add(vbo);
        FloatBuffer positionsBuffer = MemoryUtil.memCallocFloat(xrMesh.vertices().length * 3);
        for (XrVertex v : xrMesh.vertices()) {
            positionsBuffer.put(v.position().x());
            positionsBuffer.put(v.position().y());
            positionsBuffer.put(v.position().z());
        }
        positionsBuffer.flip();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, positionsBuffer, GL_STATIC_DRAW);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        MemoryUtil.memFree(positionsBuffer);

        vbo = glGenBuffers();
        vbos.add(vbo);
        FloatBuffer textCoordsBuffer = MemoryUtil.memCallocFloat(xrMesh.vertices().length * 2);
        for (XrVertex v : xrMesh.vertices()) {
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
        indicesBuffer.put(xrMesh.faces()).flip();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
        MemoryUtil.memFree(indicesBuffer);

        vbo = glGenBuffers();
        vbos.add(vbo);
        FloatBuffer vecNormalsBuffer = MemoryUtil.memAllocFloat(xrMesh.vertices().length * 3);
        for (XrVertex v : xrMesh.vertices()) {
            vecNormalsBuffer.put(v.normal().x());
            vecNormalsBuffer.put(v.normal().y());
            vecNormalsBuffer.put(v.normal().z());
        }
        vecNormalsBuffer.flip();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vecNormalsBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
        MemoryUtil.memFree(vecNormalsBuffer);

        vbo = glGenBuffers();
        vbos.add(vbo);
        FloatBuffer tangentsBuffer = MemoryUtil.memCallocFloat(xrMesh.vertices().length * 3);
        for (XrVertex v : xrMesh.vertices()) {
            tangentsBuffer.put(v.tangent().x());
            tangentsBuffer.put(v.tangent().y());
            tangentsBuffer.put(v.tangent().z());
        }
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, tangentsBuffer, GL_STATIC_DRAW);
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(3, 3, GL_FLOAT, false, 0, 0);
        MemoryUtil.memFree(tangentsBuffer);

        vbo = glGenBuffers();
        vbos.add(vbo);
        FloatBuffer bitangentsBuffer = MemoryUtil.memCallocFloat(xrMesh.vertices().length * 3);
        for (XrVertex v : xrMesh.vertices()) {
            bitangentsBuffer.put(v.bitangent().x());
            bitangentsBuffer.put(v.bitangent().y());
            bitangentsBuffer.put(v.bitangent().z());
        }
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, bitangentsBuffer, GL_STATIC_DRAW);
        glEnableVertexAttribArray(3);
        glVertexAttribPointer(4, 3, GL_FLOAT, false, 0, 0);
        MemoryUtil.memFree(bitangentsBuffer);


        vbo = glGenBuffers();
        vbos.add(vbo);
        FloatBuffer weightsBuffer = MemoryUtil.memCallocFloat(xrMesh.animationData().weights().length);
        for (Optional<XrVertexWeight> b : xrMesh.animationData().weights()) {
            weightsBuffer.put(b.map(XrVertexWeight::weight).orElse(0f));
        }
        weightsBuffer.flip();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, weightsBuffer, GL_STATIC_DRAW);
        glEnableVertexAttribArray(5);
        glVertexAttribPointer(5, 4, GL_FLOAT, false, 0, 0);
        MemoryUtil.memFree(weightsBuffer);

        vbo = glGenBuffers();
        vbos.add(vbo);
        IntBuffer boneIndicesBuffer = MemoryUtil.memCallocInt(xrMesh.animationData().bones().length);
        for (Optional<XrBone> b : xrMesh.animationData().bones()) {
            boneIndicesBuffer.put(b.map(XrBone::id).orElse(0));
        }
        boneIndicesBuffer.flip();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, boneIndicesBuffer, GL_STATIC_DRAW);
        glEnableVertexAttribArray(6);
        glVertexAttribPointer(6, 4, GL_FLOAT, false, 0, 0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        xrMesh.setVertices(null);
        xrMesh.setFaces(null);
        xrMesh.setAnimationData(null);
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

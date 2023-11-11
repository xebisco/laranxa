package com.xebisco.laranxa.opengl;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4fc;
import org.lwjgl.system.MemoryStack;

import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

public class GLShaderProgram extends GLHandler implements AutoCloseable {
    private final GLHandler vertexShader, fragmentShader;
    public static final int MAX_SPOT_LIGHTS = 10, MAX_POINT_LIGHTS = 10;
    private final Map<String, Integer> uniforms = new HashMap<>();

    public GLShaderProgram(String vertexShaderCode, String fragmentShaderCode) {
        handler = glCreateProgram();
        vertexShader = createShader(vertexShaderCode, GL_VERTEX_SHADER);
        fragmentShader = createShader(fragmentShaderCode, GL_FRAGMENT_SHADER);
        link();
    }

    public GLShaderProgram(InputStream vertexShader, InputStream fragmentShader) {
        this(GLUtils.inputStreamToString(vertexShader), GLUtils.inputStreamToString(fragmentShader));
    }

    private void createUniform(String uniformName) {
        int uniformLocation = glGetUniformLocation((int) handler, uniformName);
        if (uniformLocation < 0) {
            throw new GLException("Could not find uniform: '" + uniformName + "'");
        }
        uniforms.put(uniformName, uniformLocation);
    }

    private void checkUniform(String uniformName) {
        if(uniforms.containsKey(uniformName)) return;
        createUniform(uniformName);
    }

    public void setUniform(String uniformName, Matrix4f value) {
        checkUniform(uniformName);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer fb = stack.mallocFloat(16);
            value.get(fb);
            glUniformMatrix4fv(uniforms.get(uniformName), false, fb);
        }
    }

    public void setUniform(String uniformName, Matrix4f[] matrices) {
        checkUniform(uniformName);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            int length = matrices != null ? matrices.length : 0;
            FloatBuffer fb = stack.mallocFloat(16 * length);
            for (int i = 0; i < length; i++) {
                matrices[i].get(16 * i, fb);
            }
            glUniformMatrix4fv(uniforms.get(uniformName), false, fb);
        }
    }

    public void setUniform(String uniformName, float[][][] matrices) {
        checkUniform(uniformName);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            int length = matrices != null ? matrices.length : 0;
            FloatBuffer fb = stack.mallocFloat(16 * length);
            for (int i = 0; i < length; i++) {
                int i1 = 16 * i;
                int i2 = 0;
                while (i1 > 3) {
                    i1 -= 4;
                    i2++;
                }
                
                fb.put(matrices[i][i1][i2]);
            }
            glUniformMatrix4fv(uniforms.get(uniformName), false, fb);
        }
    }

    public void setUniform(String uniformName, Vector3fc value) {
        checkUniform(uniformName);
        glUniform3f(uniforms.get(uniformName), value.x(), value.y(), value.z());
    }

    public void setUniform(String uniformName, Vector4fc value) {
        checkUniform(uniformName);
        glUniform4f(uniforms.get(uniformName), value.x(), value.y(), value.z(), value.w());
    }

    public void setUniform(String uniformName, int value) {
        checkUniform(uniformName);
        glUniform1i(uniforms.get(uniformName), value);
    }

    public void setUniform(String uniformName, float value) {
        checkUniform(uniformName);
        glUniform1f(uniforms.get(uniformName), value);
    }

    private GLHandler createShader(String shaderCode, int shaderType) {
        int shaderId = glCreateShader(shaderType);
        if (shaderId == 0) throw new GLException("Could not create shader");

        glShaderSource(shaderId, shaderCode);
        glCompileShader(shaderId);

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) throw new GLException(glGetShaderInfoLog(shaderId, 1024));

        glAttachShader((int) handler, shaderId);

        return new GLHandler(shaderId);
    }

    public void link() {
        glLinkProgram((int) handler);

        if (glGetProgrami((int) handler, GL_LINK_STATUS) == 0) {
            throw new GLException("Could not link shader code. " + glGetProgramInfoLog((int) handler, 1024));
        }

        if (vertexShader.handler != 0) {
            glDetachShader((int) handler, (int) vertexShader.handler);
        }
        if (fragmentShader.handler != 0) {
            glDetachShader((int) handler, (int) fragmentShader.handler);
        }

        glValidateProgram((int) handler);
        if (glGetProgrami((int) handler, GL_VALIDATE_STATUS) == 0) {
            throw new GLException("Could not validate shader code. " + glGetProgramInfoLog((int) handler, 1024));
        }
    }

    public void bind() {
        glUseProgram((int) handler);
    }

    public void unbind() {
        glUseProgram(0);
    }

    @Override
    public void close() {
        unbind();
        if (handler != 0) glDeleteProgram((int) handler);
    }

    public GLHandler vertexShader() {
        return vertexShader;
    }

    public GLHandler fragmentShader() {
        return fragmentShader;
    }
}

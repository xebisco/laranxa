package com.xebisco.laranxa.opengl;

import com.xebisco.laranxa.*;
import com.xebisco.laranxa.utils.XrPoint2;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.Callback;

import static org.lwjgl.glfw.GLFW.*;

public class GLWindow extends GLHandler implements XrIWindow {

    private GLFWErrorCallback errorCallback;
    private Callback keyCallback, resizeCallback, mouseCallback;

    @Override
    public void init() {
        glfwShowWindow(handler);
    }

    @Override
    public Object process(Object self, Object value) {
        glfwSwapBuffers(handler);
        glfwPollEvents();
        return this;
    }

    @Override
    public void setup(XrWindowConfig config) {
        errorCallback = GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) {
            throw new GLFWException("Could not init glfw.");
        }

        glfwDefaultWindowHints();

        if (config.antiAliasing) glfwWindowHint(GLFW_SAMPLES, 4);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLUtils.booleanToInt(config.resizable));
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);

        handler = glfwCreateWindow((int) config.size.width(), (int) config.size.height(), config.title, 0, 0);

        if (handler == 0) throw new GLFWException("Could not create window.");

        keyCallback = glfwSetKeyCallback(handler, (_window, key, _scancode, action, _mods) -> {
            XrKey k = GLUtils.intToXrKey(key);
            if (action == GLFW_PRESS) {
                for (XrIKeyListener l : config.keyListeners) l.keyPressed(k);
            } else if (action == GLFW_RELEASE) {
                for (XrIKeyListener l : config.keyListeners) l.keyReleased(k);
            }
        });

        final XrPoint2 mousePosition = new XrPoint2();

        mouseCallback = glfwSetMouseButtonCallback(handler, (_window, button, action, mods) -> {
            XrMouseButton b = GLUtils.intToXrMouseButton(button);
            if (action == GLFW_PRESS) {
                for (XrIMouseListener l : config.mouseListeners) l.mousePressed(new XrMouseEvent(mousePosition, b));
            } else if (action == GLFW_RELEASE) {
                for (XrIMouseListener l : config.mouseListeners) l.mouseReleased(new XrMouseEvent(mousePosition, b));
            }
        });

        mouseCallback = glfwSetCursorPosCallback(handler, (_window, x, y) -> {
            for (XrIMouseListener l : config.mouseListeners) l.mouseMoved(new XrMouseEvent(mousePosition, null));
        });


        resizeCallback = glfwSetWindowSizeCallback(handler, (_window, w, h) -> {
            GL11.glViewport(0, 0, w, h);
        });

        glfwMakeContextCurrent(handler);

        if (config.vSync) glfwSwapInterval(1);
        else glfwSwapInterval(0);
    }

    @Override
    public boolean shouldClose() {
        return glfwWindowShouldClose(handler);
    }

    @Override
    public void close() {
        if (errorCallback != null) errorCallback.close();
        if (keyCallback != null) keyCallback.close();
        if (resizeCallback != null) resizeCallback.close();
        glfwTerminate();
    }
}

package com.xebisco.laranxa.opengl;

import com.xebisco.laranxa.utils.XrColor;
import com.xebisco.laranxa.XrKey;
import com.xebisco.laranxa.XrMouseButton;
import com.xebisco.laranxa.tid.XrVertex;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.assimp.AIMatrix4x4;
import org.lwjgl.glfw.GLFW;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.List;

public final class GLUtils {
    public static int booleanToInt(boolean b) {
        return b ? 1 : 0;
    }

    public static float[][] toMatrix4(AIMatrix4x4 aiMatrix4x4) {
        float[][] matrix = new float[4][4];
        matrix[0][0] = aiMatrix4x4.a1();
        matrix[1][0] = aiMatrix4x4.a2();
        matrix[2][0] = aiMatrix4x4.a3();
        matrix[3][0] = aiMatrix4x4.a4();
        matrix[0][1] = aiMatrix4x4.b1();
        matrix[1][1] = aiMatrix4x4.b2();
        matrix[2][1] = aiMatrix4x4.b3();
        matrix[3][1] = aiMatrix4x4.b4();
        matrix[0][2] = aiMatrix4x4.c1();
        matrix[1][2] = aiMatrix4x4.c2();
        matrix[2][2] = aiMatrix4x4.c3();
        matrix[3][2] = aiMatrix4x4.c4();
        matrix[0][3] = aiMatrix4x4.d1();
        matrix[1][3] = aiMatrix4x4.d2();
        matrix[2][3] = aiMatrix4x4.d3();
        matrix[3][3] = aiMatrix4x4.d4();
        return matrix;
    }

    public static float[][] toMatrix4(Matrix4f matrix4f) {
        float[][] matrix = new float[4][4];
        matrix[0][0] = matrix4f.m00();
        matrix[1][0] = matrix4f.m10();
        matrix[2][0] = matrix4f.m20();
        matrix[3][0] = matrix4f.m30();
        matrix[0][1] = matrix4f.m01();
        matrix[1][1] = matrix4f.m11();
        matrix[2][1] = matrix4f.m21();
        matrix[3][1] = matrix4f.m31();
        matrix[0][2] = matrix4f.m02();
        matrix[1][2] = matrix4f.m12();
        matrix[2][2] = matrix4f.m22();
        matrix[3][2] = matrix4f.m32();
        matrix[0][3] = matrix4f.m03();
        matrix[1][3] = matrix4f.m13();
        matrix[2][3] = matrix4f.m23();
        matrix[3][3] = matrix4f.m33();
        return matrix;
    }

    public static Matrix4f toMatrix4f(float[][] matrix) {
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.m00(matrix[0][0]);
        matrix4f.m10(matrix[1][0]);
        matrix4f.m20(matrix[2][0]);
        matrix4f.m30(matrix[3][0]);
        matrix4f.m01(matrix[0][1]);
        matrix4f.m11(matrix[1][1]);
        matrix4f.m21(matrix[2][1]);
        matrix4f.m31(matrix[3][1]);
        matrix4f.m02(matrix[0][2]);
        matrix4f.m12(matrix[1][2]);
        matrix4f.m22(matrix[2][2]);
        matrix4f.m32(matrix[3][2]);
        matrix4f.m03(matrix[0][3]);
        matrix4f.m13(matrix[1][3]);
        matrix4f.m23(matrix[2][3]);
        matrix4f.m33(matrix[3][3]);
        return matrix4f;
    }

    public static Matrix4f toMatrix4f(AIMatrix4x4 aiMatrix4x4) {
        Matrix4f matrix = new Matrix4f();
        matrix.m00(aiMatrix4x4.a1());
        matrix.m10(aiMatrix4x4.a2());
        matrix.m20(aiMatrix4x4.a3());
        matrix.m30(aiMatrix4x4.a4());
        matrix.m01(aiMatrix4x4.b1());
        matrix.m11(aiMatrix4x4.b2());
        matrix.m21(aiMatrix4x4.b3());
        matrix.m31(aiMatrix4x4.b4());
        matrix.m02(aiMatrix4x4.c1());
        matrix.m12(aiMatrix4x4.c2());
        matrix.m22(aiMatrix4x4.c3());
        matrix.m32(aiMatrix4x4.c4());
        matrix.m03(aiMatrix4x4.d1());
        matrix.m13(aiMatrix4x4.d2());
        matrix.m23(aiMatrix4x4.d3());
        matrix.m33(aiMatrix4x4.d4());
        return matrix;
    }

    public static ByteBuffer inputStreamToByteBuffer(InputStream is, int bufferSize) throws IOException {
        ByteBuffer buffer;

        try (ReadableByteChannel rbc = Channels.newChannel(new BufferedInputStream(is))) {
            buffer = BufferUtils.createByteBuffer(bufferSize);

            while (true) {
                int bytes = rbc.read(buffer);
                if (bytes == -1) {
                    break;
                }
                if (buffer.remaining() == 0) {
                    buffer = resizeBuffer(buffer, buffer.capacity() * 2);
                }
            }
        }

        buffer.flip();
        return buffer;
    }

    private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }

    public static Vector4f colorToVector4f(XrColor color) {
        return new Vector4f(color.red(), color.green(), color.blue(), color.alpha());
    }

    public static Vector3f colorToVector3f(XrColor color) {
        return new Vector3f(color.red(), color.green(), color.blue());
    }

    public static String inputStreamToString(InputStream is) {
        StringBuilder c = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(is)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                c.append(line).append('\n');
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return c.toString();
    }

    public static XrVertex[] toVertexArray(List<XrVertex> verticesList) {
        XrVertex[] verticesArr = new XrVertex[verticesList.size()];
        for (int i = 0; i < verticesArr.length; i++) verticesArr[i] = verticesList.get(i);
        return verticesArr;
    }

    public static int[] toIntArray(List<Integer> intList) {
        int[] intArr = new int[intList.size()];
        for (int i = 0; i < intArr.length; i++) intArr[i] = intList.get(i);
        return intArr;
    }

    public static XrMouseButton intToXrMouseButton(int i) {
        switch (i) {
            case GLFW.GLFW_MOUSE_BUTTON_LEFT:
                return XrMouseButton.LEFT_BUTTON;
            case GLFW.GLFW_MOUSE_BUTTON_RIGHT:
                return XrMouseButton.RIGHT_BUTTON;
            case GLFW.GLFW_MOUSE_BUTTON_MIDDLE:
                return XrMouseButton.MIDDLE_BUTTON;
            case GLFW.GLFW_MOUSE_BUTTON_4:
                return XrMouseButton.BUTTON_4;
            case GLFW.GLFW_MOUSE_BUTTON_5:
                return XrMouseButton.BUTTON_5;
        }
        return XrMouseButton.UNDEFINED;
    }

    public static XrKey intToXrKey(int k) {
        switch (k) {
            case GLFW.GLFW_KEY_SPACE:
                return XrKey.KEY_SPACE;
            case GLFW.GLFW_KEY_APOSTROPHE:
                return XrKey.KEY_APOSTROPHE;
            case GLFW.GLFW_KEY_COMMA:
                return XrKey.KEY_COMMA;
            case GLFW.GLFW_KEY_MINUS:
                return XrKey.KEY_MINUS;
            case GLFW.GLFW_KEY_PERIOD:
                return XrKey.KEY_PERIOD;
            case GLFW.GLFW_KEY_SLASH:
                return XrKey.KEY_SLASH;
            case GLFW.GLFW_KEY_0:
                return XrKey.KEY_0;
            case GLFW.GLFW_KEY_1:
                return XrKey.KEY_1;
            case GLFW.GLFW_KEY_2:
                return XrKey.KEY_2;
            case GLFW.GLFW_KEY_3:
                return XrKey.KEY_3;
            case GLFW.GLFW_KEY_4:
                return XrKey.KEY_4;
            case GLFW.GLFW_KEY_5:
                return XrKey.KEY_5;
            case GLFW.GLFW_KEY_6:
                return XrKey.KEY_6;
            case GLFW.GLFW_KEY_7:
                return XrKey.KEY_7;
            case GLFW.GLFW_KEY_8:
                return XrKey.KEY_8;
            case GLFW.GLFW_KEY_9:
                return XrKey.KEY_9;
            case GLFW.GLFW_KEY_SEMICOLON:
                return XrKey.KEY_SEMICOLON;
            case GLFW.GLFW_KEY_EQUAL:
                return XrKey.KEY_EQUAL;
            case GLFW.GLFW_KEY_A:
                return XrKey.KEY_A;
            case GLFW.GLFW_KEY_B:
                return XrKey.KEY_B;
            case GLFW.GLFW_KEY_C:
                return XrKey.KEY_C;
            case GLFW.GLFW_KEY_D:
                return XrKey.KEY_D;
            case GLFW.GLFW_KEY_E:
                return XrKey.KEY_E;
            case GLFW.GLFW_KEY_F:
                return XrKey.KEY_F;
            case GLFW.GLFW_KEY_G:
                return XrKey.KEY_G;
            case GLFW.GLFW_KEY_H:
                return XrKey.KEY_H;
            case GLFW.GLFW_KEY_I:
                return XrKey.KEY_I;
            case GLFW.GLFW_KEY_J:
                return XrKey.KEY_J;
            case GLFW.GLFW_KEY_K:
                return XrKey.KEY_K;
            case GLFW.GLFW_KEY_L:
                return XrKey.KEY_L;
            case GLFW.GLFW_KEY_M:
                return XrKey.KEY_M;
            case GLFW.GLFW_KEY_N:
                return XrKey.KEY_N;
            case GLFW.GLFW_KEY_O:
                return XrKey.KEY_O;
            case GLFW.GLFW_KEY_P:
                return XrKey.KEY_P;
            case GLFW.GLFW_KEY_Q:
                return XrKey.KEY_Q;
            case GLFW.GLFW_KEY_R:
                return XrKey.KEY_R;
            case GLFW.GLFW_KEY_S:
                return XrKey.KEY_S;
            case GLFW.GLFW_KEY_T:
                return XrKey.KEY_T;
            case GLFW.GLFW_KEY_U:
                return XrKey.KEY_U;
            case GLFW.GLFW_KEY_V:
                return XrKey.KEY_V;
            case GLFW.GLFW_KEY_W:
                return XrKey.KEY_W;
            case GLFW.GLFW_KEY_X:
                return XrKey.KEY_X;
            case GLFW.GLFW_KEY_Y:
                return XrKey.KEY_Y;
            case GLFW.GLFW_KEY_Z:
                return XrKey.KEY_Z;
            case GLFW.GLFW_KEY_LEFT_BRACKET:
                return XrKey.KEY_LEFT_BRACKET;
            case GLFW.GLFW_KEY_BACKSLASH:
                return XrKey.KEY_BACKSLASH;
            case GLFW.GLFW_KEY_RIGHT_BRACKET:
                return XrKey.KEY_RIGHT_BRACKET;
            case GLFW.GLFW_KEY_GRAVE_ACCENT:
                return XrKey.KEY_GRAVE_ACCENT;
            case GLFW.GLFW_KEY_WORLD_1:
                return XrKey.KEY_WORLD_1;
            case GLFW.GLFW_KEY_WORLD_2:
                return XrKey.KEY_WORLD_2;
            case GLFW.GLFW_KEY_ESCAPE:
                return XrKey.KEY_ESCAPE;
            case GLFW.GLFW_KEY_ENTER:
                return XrKey.KEY_ENTER;
            case GLFW.GLFW_KEY_TAB:
                return XrKey.KEY_TAB;
            case GLFW.GLFW_KEY_BACKSPACE:
                return XrKey.KEY_BACKSPACE;
            case GLFW.GLFW_KEY_INSERT:
                return XrKey.KEY_INSERT;
            case GLFW.GLFW_KEY_DELETE:
                return XrKey.KEY_DELETE;
            case GLFW.GLFW_KEY_RIGHT:
                return XrKey.KEY_RIGHT;
            case GLFW.GLFW_KEY_LEFT:
                return XrKey.KEY_LEFT;
            case GLFW.GLFW_KEY_DOWN:
                return XrKey.KEY_DOWN;
            case GLFW.GLFW_KEY_UP:
                return XrKey.KEY_UP;
            case GLFW.GLFW_KEY_PAGE_UP:
                return XrKey.KEY_PAGE_UP;
            case GLFW.GLFW_KEY_PAGE_DOWN:
                return XrKey.KEY_PAGE_DOWN;
            case GLFW.GLFW_KEY_HOME:
                return XrKey.KEY_HOME;
            case GLFW.GLFW_KEY_END:
                return XrKey.KEY_END;
            case GLFW.GLFW_KEY_CAPS_LOCK:
                return XrKey.KEY_CAPS_LOCK;
            case GLFW.GLFW_KEY_SCROLL_LOCK:
                return XrKey.KEY_SCROLL_LOCK;
            case GLFW.GLFW_KEY_NUM_LOCK:
                return XrKey.KEY_NUM_LOCK;
            case GLFW.GLFW_KEY_PRINT_SCREEN:
                return XrKey.KEY_PRINT_SCREEN;
            case GLFW.GLFW_KEY_PAUSE:
                return XrKey.KEY_PAUSE;
            case GLFW.GLFW_KEY_F1:
                return XrKey.KEY_F1;
            case GLFW.GLFW_KEY_F2:
                return XrKey.KEY_F2;
            case GLFW.GLFW_KEY_F3:
                return XrKey.KEY_F3;
            case GLFW.GLFW_KEY_F4:
                return XrKey.KEY_F4;
            case GLFW.GLFW_KEY_F5:
                return XrKey.KEY_F5;
            case GLFW.GLFW_KEY_F6:
                return XrKey.KEY_F6;
            case GLFW.GLFW_KEY_F7:
                return XrKey.KEY_F7;
            case GLFW.GLFW_KEY_F8:
                return XrKey.KEY_F8;
            case GLFW.GLFW_KEY_F9:
                return XrKey.KEY_F9;
            case GLFW.GLFW_KEY_F10:
                return XrKey.KEY_F10;
            case GLFW.GLFW_KEY_F11:
                return XrKey.KEY_F11;
            case GLFW.GLFW_KEY_F12:
                return XrKey.KEY_F12;
            case GLFW.GLFW_KEY_F13:
                return XrKey.KEY_F13;
            case GLFW.GLFW_KEY_F14:
                return XrKey.KEY_F14;
            case GLFW.GLFW_KEY_F15:
                return XrKey.KEY_F15;
            case GLFW.GLFW_KEY_F16:
                return XrKey.KEY_F16;
            case GLFW.GLFW_KEY_F17:
                return XrKey.KEY_F17;
            case GLFW.GLFW_KEY_F18:
                return XrKey.KEY_F18;
            case GLFW.GLFW_KEY_F19:
                return XrKey.KEY_F19;
            case GLFW.GLFW_KEY_F20:
                return XrKey.KEY_F20;
            case GLFW.GLFW_KEY_F21:
                return XrKey.KEY_F21;
            case GLFW.GLFW_KEY_F22:
                return XrKey.KEY_F22;
            case GLFW.GLFW_KEY_F23:
                return XrKey.KEY_F23;
            case GLFW.GLFW_KEY_F24:
                return XrKey.KEY_F24;
            case GLFW.GLFW_KEY_F25:
                return XrKey.KEY_F25;
            case GLFW.GLFW_KEY_KP_0:
                return XrKey.KEY_KP_0;
            case GLFW.GLFW_KEY_KP_1:
                return XrKey.KEY_KP_1;
            case GLFW.GLFW_KEY_KP_2:
                return XrKey.KEY_KP_2;
            case GLFW.GLFW_KEY_KP_3:
                return XrKey.KEY_KP_3;
            case GLFW.GLFW_KEY_KP_4:
                return XrKey.KEY_KP_4;
            case GLFW.GLFW_KEY_KP_5:
                return XrKey.KEY_KP_5;
            case GLFW.GLFW_KEY_KP_6:
                return XrKey.KEY_KP_6;
            case GLFW.GLFW_KEY_KP_7:
                return XrKey.KEY_KP_7;
            case GLFW.GLFW_KEY_KP_8:
                return XrKey.KEY_KP_8;
            case GLFW.GLFW_KEY_KP_9:
                return XrKey.KEY_KP_9;
            case GLFW.GLFW_KEY_KP_DECIMAL:
                return XrKey.KEY_KP_DECIMAL;
            case GLFW.GLFW_KEY_KP_DIVIDE:
                return XrKey.KEY_KP_DIVIDE;
            case GLFW.GLFW_KEY_KP_MULTIPLY:
                return XrKey.KEY_KP_MULTIPLY;
            case GLFW.GLFW_KEY_KP_SUBTRACT:
                return XrKey.KEY_KP_SUBTRACT;
            case GLFW.GLFW_KEY_KP_ADD:
                return XrKey.KEY_KP_ADD;
            case GLFW.GLFW_KEY_KP_ENTER:
                return XrKey.KEY_KP_ENTER;
            case GLFW.GLFW_KEY_KP_EQUAL:
                return XrKey.KEY_KP_EQUAL;
            case GLFW.GLFW_KEY_LEFT_SHIFT:
                return XrKey.KEY_LEFT_SHIFT;
            case GLFW.GLFW_KEY_LEFT_CONTROL:
                return XrKey.KEY_LEFT_CONTROL;
            case GLFW.GLFW_KEY_LEFT_ALT:
                return XrKey.KEY_LEFT_ALT;
            case GLFW.GLFW_KEY_LEFT_SUPER:
                return XrKey.KEY_LEFT_SUPER;
            case GLFW.GLFW_KEY_RIGHT_SHIFT:
                return XrKey.KEY_RIGHT_SHIFT;
            case GLFW.GLFW_KEY_RIGHT_CONTROL:
                return XrKey.KEY_RIGHT_CONTROL;
            case GLFW.GLFW_KEY_RIGHT_ALT:
                return XrKey.KEY_RIGHT_ALT;
            case GLFW.GLFW_KEY_RIGHT_SUPER:
                return XrKey.KEY_RIGHT_SUPER;
            case GLFW.GLFW_KEY_MENU:
                return XrKey.KEY_MENU;
        }
        return XrKey.UNDEFINED;
    }
}

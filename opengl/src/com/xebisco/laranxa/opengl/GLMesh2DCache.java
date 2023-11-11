package com.xebisco.laranxa.opengl;

import com.xebisco.laranxa.bid.object.XrMesh2D;
import com.xebisco.laranxa.tid.object.XrMesh;

import java.util.HashMap;
import java.util.Map;

public class GLMesh2DCache {
    private static final Map<XrMesh2D, GLMesh2D> meshMap = new HashMap<>();

    public static GLMesh2D get(XrMesh2D mesh) {
        if(meshMap.containsKey(mesh)) return meshMap.get(mesh);
        GLMesh2D glMesh = new GLMesh2D(mesh);
        meshMap.put(mesh, glMesh);
        return glMesh;
    }

    public static void clear() {
        meshMap.values().forEach(GLMesh2D::close);
        meshMap.clear();
    }
}

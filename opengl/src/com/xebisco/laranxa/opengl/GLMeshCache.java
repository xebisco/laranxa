package com.xebisco.laranxa.opengl;

import com.xebisco.laranxa.tid.object.XrMesh;

import java.util.HashMap;
import java.util.Map;

public class GLMeshCache {
    private static final Map<XrMesh, GLMesh> meshMap = new HashMap<>();

    public static GLMesh get(XrMesh mesh) {
        if(meshMap.containsKey(mesh)) return meshMap.get(mesh);
        GLMesh glMesh = new GLMesh(mesh);
        meshMap.put(mesh, glMesh);
        return glMesh;
    }

    public static void clear() {
        meshMap.values().forEach(GLMesh::close);
        meshMap.clear();
    }
}

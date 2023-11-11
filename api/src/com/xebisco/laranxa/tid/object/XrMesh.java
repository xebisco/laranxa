package com.xebisco.laranxa.tid.object;

import com.xebisco.laranxa.tid.XrVertex;
import com.xebisco.laranxa.tid.animation.XrMeshAnimationData;

public class XrMesh {
    private XrVertex[] vertices;
    private int[] faces;
    private XrMeshAnimationData animationData;

    public XrMesh(XrVertex[] vertices, int[] faces, XrMeshAnimationData animationData) {
        this.vertices = vertices;
        this.faces = faces;
        this.animationData = animationData;
    }

    public XrVertex[] vertices() {
        return vertices;
    }

    public int[] faces() {
        return faces;
    }

    public void setVertices(XrVertex[] vertices) {
        this.vertices = vertices;
    }

    public void setFaces(int[] faces) {
        this.faces = faces;
    }

    public XrMeshAnimationData animationData() {
        return animationData;
    }

    public void setAnimationData(XrMeshAnimationData animationData) {
        this.animationData = animationData;
    }
}

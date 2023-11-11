package com.xebisco.laranxa.bid.object;

import com.xebisco.laranxa.bid.XrVertex2D;

public final class XrMesh2D {
    private XrVertex2D[] vertices;
    private int[] indices;

    public XrMesh2D(XrVertex2D[] vertices, int[] indices) {
        this.vertices = vertices;
        this.indices = indices;
    }

    public XrVertex2D[] vertices() {
        return vertices;
    }

    public XrMesh2D setVertices(XrVertex2D[] vertices) {
        this.vertices = vertices;
        return this;
    }

    public int[] indices() {
        return indices;
    }

    public XrMesh2D setIndices(int[] indices) {
        this.indices = indices;
        return this;
    }
}

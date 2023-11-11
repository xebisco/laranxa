package com.xebisco.laranxa.tid.object;

public final class XrModel {
    private final XrMesh mesh;
    private final XrMaterial material;

    public XrModel(XrMesh mesh, XrMaterial material) {
        this.mesh = mesh;
        this.material = material;
    }

    public XrMesh mesh() {
        return mesh;
    }

    public XrMaterial material() {
        return material;
    }
}

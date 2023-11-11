package com.xebisco.laranxa.opengl;

import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public record GLAINode(String name, GLAINode parent, Matrix4f nodeTransformation, List<GLAINode> children) {
    public GLAINode(String name, GLAINode parent, Matrix4f nodeTransformation) {
        this(name, parent, nodeTransformation, new ArrayList<>());
    }
}

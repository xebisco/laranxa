package com.xebisco.laranxa.tid.animation;

import java.util.Optional;

public record XrMeshAnimationData(Optional<XrVertexWeight>[] weights, Optional<XrBone>[] bones) {
}

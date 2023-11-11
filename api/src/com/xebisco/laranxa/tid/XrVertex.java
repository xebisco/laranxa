package com.xebisco.laranxa.tid;

import com.xebisco.laranxa.utils.XrIPoint2;
import com.xebisco.laranxa.utils.XrIPoint3;

public record XrVertex(XrIPoint3 position, XrIPoint3 normal, XrIPoint3 tangent, XrIPoint3 bitangent,
                       XrIPoint2 textureCoord) {
}

package com.xebisco.laranxa.tid.object;

import com.xebisco.laranxa.tid.animation.XrAnimation;
import com.xebisco.laranxa.tid.animation.XrObjectAnimationData;

import java.util.Map;

public record XrObject(XrModel[] models, XrObjectAnimationData objectAnimationData, Map<String, XrAnimation> animationMap) {
}

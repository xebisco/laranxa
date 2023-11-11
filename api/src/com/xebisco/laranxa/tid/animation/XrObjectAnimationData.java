package com.xebisco.laranxa.tid.animation;

public class XrObjectAnimationData {
    private XrAnimation currentAnimation;
    private int currentFrameIndex;

    public XrAnimation currentAnimation() {
        return currentAnimation;
    }

    public XrAnimatedFrame currentFrame() {
        return currentAnimation.frames()[currentFrameIndex];
    }

    public int currentFrameIndex() {
        return currentFrameIndex;
    }

    public void nextFrame() {
        int nextFrame = currentFrameIndex + 1;
        if (nextFrame > currentAnimation.frames().length - 1) {
            currentFrameIndex = 0;
        } else {
            currentFrameIndex = nextFrame;
        }
    }

    public XrObjectAnimationData setCurrentAnimation(XrAnimation currentAnimation) {
        currentFrameIndex = 0;
        this.currentAnimation = currentAnimation;
        return this;
    }
}

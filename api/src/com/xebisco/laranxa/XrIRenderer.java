package com.xebisco.laranxa;

import com.xebisco.laranxa.bid.XrCamera2D;
import com.xebisco.laranxa.bid.XrTransformation2D;
import com.xebisco.laranxa.bid.object.XrObject2D;
import com.xebisco.laranxa.tid.XrProjectionConfig;
import com.xebisco.laranxa.tid.XrTransformation;
import com.xebisco.laranxa.tid.lightning.XrSceneLightning;
import com.xebisco.laranxa.tid.object.XrObject;
import com.xebisco.laranxa.tid.XrCamera;

public interface XrIRenderer extends XrIInit {
    void clearFramebuffer();
    void draw3D(XrObject object, XrTransformation transformation, XrCamera camera, XrSceneLightning sceneLightning);
    void draw2D(XrObject2D object, XrTransformation2D transformation, XrCamera2D camera);
    void updateProjection(XrProjectionConfig projectionConfig, float aspectRatio);
}

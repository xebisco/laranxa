package com.xebisco.laranxa.tid.lightning;

import com.xebisco.laranxa.utils.XrColor;
import com.xebisco.laranxa.utils.XrPoint3;

import java.util.List;

public record XrSceneLightning(XrDirectionalLight directionalLight, XrColor ambientLight,
                               List<XrPointLight> pointLights, List<XrSpotLight> spotLights) {

    public XrSceneLightning(List<XrPointLight> pointLights, List<XrSpotLight> spotLights) {
        this(new XrDirectionalLight(new XrColor(1, 1, 1), new XrPoint3(0, 1, 1), 1), new XrColor(1, 1, 1), pointLights, spotLights);
    }
}

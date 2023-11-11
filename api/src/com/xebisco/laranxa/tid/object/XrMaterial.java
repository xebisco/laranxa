package com.xebisco.laranxa.tid.object;

import com.xebisco.laranxa.utils.XrImmutableColor;
import com.xebisco.laranxa.XrTexture;
import com.xebisco.laranxa.utils.XrColor;

public record XrMaterial(XrTexture diffuseMap, XrTexture normalMap, XrColor ambientColor, XrColor diffuseColor,
                         XrColor specularColor, float reflectance, boolean hasTransparency) {
    public static final XrImmutableColor DEFAULT_COLOR = new XrImmutableColor(0xFF4DA6FF, XrColor.Format.ARGB);
}

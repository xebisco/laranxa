package com.xebisco.laranxa.tid.object;

import com.xebisco.laranxa.tid.object.XrMaterial;
import com.xebisco.laranxa.tid.object.XrObject;

import java.io.File;

public interface XrIObjectLoader {
    default XrObject loadObject(File file, boolean animation) {
        return loadObject(file, null, animation);
    }
    XrObject loadObject(File file, XrMaterial material, boolean animation);
}

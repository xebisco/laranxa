package com.xebisco.laranxa.utils;

public interface XrIPoint3 extends XrIPoint2 {
    float z();

    XrIPoint3 add(XrIPoint3 v);
    XrIPoint3 sub(XrIPoint3 v);
    XrIPoint3 mul(XrIPoint3 v);
    XrIPoint3 div(XrIPoint3 v);
}

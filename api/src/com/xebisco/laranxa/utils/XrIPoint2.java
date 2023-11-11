package com.xebisco.laranxa.utils;

public interface XrIPoint2 {
    float x();
    float y();

    XrIPoint2 add(XrIPoint2 v);
    XrIPoint2 sub(XrIPoint2 v);
    XrIPoint2 mul(XrIPoint2 v);
    XrIPoint2 div(XrIPoint2 v);
}

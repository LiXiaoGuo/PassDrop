package com.yuzhua.aspectj;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 允许快速点击，即不做拦截
 * Created by Extends on 2018/5/4/004.
 */

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface FastClick{}

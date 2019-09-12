package com.linxiao.framework.fragment

import java.lang.annotation.RetentionPolicy

/**
 * Created by Extends on 2018/4/14/014.
 */

/**
 * 关闭懒加载
 */
@Retention
@Target(AnnotationTarget.CLASS)
annotation class NoLazyFragment

/**
 * 开启Event
 */
@Retention
@Target(AnnotationTarget.CLASS)
annotation class StartEvent

/**
 * 横屏
 */
@Retention
@Target(AnnotationTarget.CLASS)
annotation class ScreenLandscape

/**
 * 开启ARouter自动注解
 */
@Retention
@Target(AnnotationTarget.CLASS)
annotation class RouterAutoInject
package com.yuzhua.aspectj;

import android.util.Log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

/**
 * 一些测试用的
 * @author Extends
 * @date 2018/5/4
 */

@Aspect
public class AspectTest {

    private static final String TAG = "----------";

//    @Before("execution(* android.app.Activity.on**(..))")
//    public void onActivityMethodBefore(JoinPoint joinPoint) throws Throwable {
//        String key = joinPoint.getSignature().toString();
//        Log.d(TAG, "onActivityMethodBefore: " + key);
//    }

//    @Before("execution(* android.view.View.OnClickListener.onClick(..))")
//    public void onActivityMethodBefore1(JoinPoint joinPoint) throws Throwable {
//        String key = joinPoint.getSignature().toString();
//        Log.d("==============", "onActivityMethodBefore: " + key);
//    }
//
//    @Before("execution(* com.yuzhua.testaspectj.MainActivity.test())")
//    public void onActivityMethodBefore2(JoinPoint joinPoint) throws Throwable {
//        String key = joinPoint.getSignature().toString();
//        Log.d("-=-=-=-=-=-=-=-=-=", "onActivityMethodBefore: " + key);
//    }
}

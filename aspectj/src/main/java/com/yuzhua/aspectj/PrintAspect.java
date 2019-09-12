package com.yuzhua.aspectj;

import android.os.SystemClock;
import android.view.View;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

/**
 * 打印事件的切入
 * @author Extends
 * @date 2018/5/4
 */

//@Aspect
//public class PrintAspect {
//
//    private static final String TAG="PrintAspect";
//
//    /**
//     * 是否允许打印
//     */
//    private boolean isAllowPrinting = false;
//
//    @Around("execution(* java.io.PrintStream.print(..))")
//    public void print(ProceedingJoinPoint joinPoint) throws Throwable{
//        //判断是否允许打印
//        if(isAllowPrinting){
//            joinPoint.proceed();
//        }
//    }
//
//    @Around("execution(* android.util.Log.e(..))")
//    public void printlnNative(ProceedingJoinPoint joinPoint) throws Throwable{
//        //判断是否允许打印
//        if(isAllowPrinting){
//            joinPoint.proceed();
//        }
//    }
//}

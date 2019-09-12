package com.linxiao.framework.util

import com.blankj.utilcode.util.LogUtils

/**
 * 打印控制
 * 凡是需要在控制台打印的方法，不管是log打印，还是print打印
 * @author Extends
 * @date 2019/9/5/005
 */

fun Any?.logv(tag:String?=null){
    val temp = LogUtils.getConfig().stackOffset
    if(tag == null){
        LogUtils.getConfig().stackOffset = 2
        LogUtils.v(this?:"value is null")
    } else{
        LogUtils.getConfig().stackOffset = 1
        LogUtils.vTag(tag,this?:"value is null")
    }
    LogUtils.getConfig().stackOffset = temp
}

fun Any?.logd(tag:String?=null){
    val temp = LogUtils.getConfig().stackOffset
    if(tag == null){
        LogUtils.getConfig().stackOffset = 2
        LogUtils.d(this?:"value is null")
    } else{
        LogUtils.getConfig().stackOffset = 1
        LogUtils.dTag(tag,this?:"value is null")
    }
    LogUtils.getConfig().stackOffset = temp
}

fun Any?.logi(tag:String?=null){
    val temp = LogUtils.getConfig().stackOffset
    if(tag == null){
        LogUtils.getConfig().stackOffset = 2
        LogUtils.i(this?:"value is null")
    } else{
        LogUtils.getConfig().stackOffset = 1
        LogUtils.iTag(tag,this?:"value is null")
    }
    LogUtils.getConfig().stackOffset = temp
}

fun Any?.logw(tag:String?=null){
    val temp = LogUtils.getConfig().stackOffset
    if(tag == null){
        LogUtils.getConfig().stackOffset = 2
        LogUtils.w(this?:"value is null")
    } else{
        LogUtils.getConfig().stackOffset = 1
        LogUtils.wTag(tag,this?:"value is null")
    }
    LogUtils.getConfig().stackOffset = temp
}

fun Any?.loge(tag:String?=null){
    val temp = LogUtils.getConfig().stackOffset
    if(tag == null){
        LogUtils.getConfig().stackOffset = 2
        LogUtils.e(this?:"value is null")
    } else{
        LogUtils.getConfig().stackOffset = 1
        LogUtils.eTag(tag,this?:"value is null")
    }
    LogUtils.getConfig().stackOffset = temp
}

fun Any?.loga(tag:String?=null){
    val temp = LogUtils.getConfig().stackOffset
    if(tag == null){
        LogUtils.getConfig().stackOffset = 2
        LogUtils.a(this?:"value is null")
    } else{
        LogUtils.getConfig().stackOffset = 1
        LogUtils.aTag(tag,this?:"value is null")
    }
    LogUtils.getConfig().stackOffset = temp
}

fun Any?.println(){
    if(LogUtils.getConfig().isLogSwitch && LogUtils.getConfig().isLog2ConsoleSwitch){
        System.out.println(this?:"value is null")
    }
}
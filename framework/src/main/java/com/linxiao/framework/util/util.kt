package com.linxiao.framework.util

import android.Manifest
import android.content.Intent
import android.net.Uri
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.RegexUtils
import org.jetbrains.anko.makeCall

/**
 * 一些不成套的公共方法
 * @author Extends
 * @date 2019/6/14/014
 */

object Util{

    /**
     * 打开qq和指定的qq联系
     * 注：指定qq好像只能是开通了客服功能的QQ号
     */
    fun contactFromQq(qq: String?){
        if(qq.isNullOrBlank()) {
            "客服忙，请稍后再试".toast()
            return
        }
        try {
            val url = "mqqwpa://im/chat?chat_type=wpa&uin=$qq"//uin是发送过去的qq号码
            ActivityUtils.getTopActivity().startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }catch (e:Exception){
            e.printStackTrace()
            "您的手机还没有安装QQ".toast()
        }
    }

    /**
     * 直接拨打电话
     */
    fun contactFromMobile(phoneNumber: String?){
        if(phoneNumber.isNullOrBlank()) {
            "客服忙，请稍后再试".toast()
            return
        }
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("tel:$phoneNumber"))
            ActivityUtils.getTopActivity().startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
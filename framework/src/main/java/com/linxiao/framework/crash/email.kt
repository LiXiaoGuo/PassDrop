package com.linxiao.framework.crash

import android.util.Log
import com.linxiao.framework.BaseApplication
import com.linxiao.framework.util.println
import java.util.*

/**
 *
 * @author Extends
 * @date 2019/8/13/013
 */


/**
 * 发送邮件，使用自己的邮件地址
 */
fun sendSelfEmail(subject:String,content:String,receiveEmail:Array<String>,userName:String="1043274460@qq.com",password:String="gkfbkpfltrnlbfeh"){
    try {
        val e = MailSenderInfo()
        e.mailServerHost = "smtp.qq.com"
        e.mailServerPort = "465"
        e.isValidate = true
        e.userName = userName
        e.password = password
        e.setFromAddress(userName,BaseApplication.getApplicationName())
        e.setToAddress(receiveEmail)
        e.subject = subject
        e.content = content
        val sms = SimpleMailSender()
        val b = sms.sendTextMail(e)
        b.println()
    } catch (var4: Exception) {
        Log.e("SendMail", var4.message, var4)
    }
}


package com.linxiao.framework.crash


import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Looper
import android.os.Process
import android.util.Log
import android.widget.Toast
import org.jetbrains.anko.doAsync
import java.io.PrintWriter
import java.io.StringWriter
import java.lang.Thread.UncaughtExceptionHandler
import java.text.SimpleDateFormat
import java.util.*

/**
 * 目前仅支持qq邮箱发送邮件,如需添加其他邮箱,请自行修改
 * @see sentEmail
 * * Created by Extends on 2016/5/9 0009.
 */
class CrashLog private constructor() : UncaughtExceptionHandler {
    private var receiveEmail: Array<out String>? = null
    private lateinit var username: String
    private lateinit var password: String
    private lateinit var nickname: String
    private lateinit var mDefaultHandler: UncaughtExceptionHandler
    private lateinit var mContext: Context
    private val mLogInfo = HashMap<String,String>()
    @SuppressLint("SimpleDateFormat")
    private val mSimpleDateFormat = SimpleDateFormat("yyyyMMdd:HH-mm-ss")

    fun init(paramContext: Context, username: String, password: String, nickname: String, vararg receiveEmail: String):CrashLog {
        this.receiveEmail = receiveEmail
        this.mContext = paramContext
        this.username = username
        this.password = password
        this.nickname = nickname
        this.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(this)
        return this
    }


    override fun uncaughtException(thread: Thread, ex: Throwable) {
        if (!this.handleException(ex)) {
            this.mDefaultHandler.uncaughtException(thread, ex)
        } else {
            try {
                Thread.sleep(2000L)
            } catch (var4: Exception) {
                var4.printStackTrace()
            }

            Process.killProcess(Process.myPid())
            System.exit(1)
        }
    }

    fun handleException(paramThrowable: Throwable?): Boolean {
        if (paramThrowable == null) {
            return false
        } else {
            doAsync {
                Looper.prepare()
                Toast.makeText(mContext, "很抱歉,程序出现异常,即将退出.", Toast.LENGTH_LONG).show()
                getDeviceInfo(mContext)
                val log = getCrashLog(paramThrowable)
                sentEmail(log)
                Looper.loop()
            }

            return true
        }
    }

    /**
     * 如需修改发送者的邮箱,只需要修改mailServerHost和mailServerPort
     */
    private fun sentEmail(paramThrowable: String) {
        try {
            val e = MailSenderInfo()
            e.mailServerHost = "smtp.qq.com"
            e.mailServerPort = "465"
            e.isValidate = true
            e.userName = this.username//
            e.password = this.password//
            e.setFromAddress(this.username, this.nickname)
            e.setToAddress(this.receiveEmail)
            e.subject = mContext.packageName + " " + mLogInfo["BRAND"] + "/" + mLogInfo["MODEL"] + " " + this.mSimpleDateFormat.format(Date())
            e.content = paramThrowable
            val sms = SimpleMailSender()
            Log.e(TAG, "sentEmail")
            val b = sms.sendTextMail(e)
            Log.e(TAG, b.toString() + "")
        } catch (var4: Exception) {
            Log.e("SendMail", var4.message, var4)
        }

        //        SendMail sendmail = new SendMail();
        //        sendmail.setHost("smtp.qq.com");//smtp.mail.yahoo.com.cn
        //        sendmail.setUserName("1043274460@qq.com");//您的邮箱用户名
        //        sendmail.setPassWord("2010YYandLG");//您的邮箱密码
        //        sendmail.setTo(receiveEmail);//接收者
        //        sendmail.setFrom("1043274460@qq.com");//发送者
        //        sendmail.setSubject("你好，这是测试2！");
        //        sendmail.setContent("你好这是一个带多附件的测试2！");
        //        boolean b = sendmail.sendMail();

    }

    fun getDeviceInfo(paramContext: Context) {
        try {
            val mFields = paramContext.packageManager
            val field = mFields.getPackageInfo(paramContext.packageName, PackageManager.GET_ACTIVITIES)
            if (field != null) {
                val versionName = if (field.versionName == null) "null" else field.versionName
                val versionCode = field.versionCode.toString()
                this.mLogInfo.put("versionName", versionName)
                this.mLogInfo.put("versionCode", versionCode)
            }
        } catch (var10: PackageManager.NameNotFoundException) {
            var10.printStackTrace()
        }

        val var11 = Build::class.java.declaredFields
        val var6 = var11
        val var14 = var11.size

        (0..var14 - 1)
                .asSequence()
                .map { var6[it] }
                .forEach {
                    try {
                        it.isAccessible = true
                        this.mLogInfo.put(it.name, it.get("").toString())
                        Log.e("NorrisInfo", it.name + ":" + it.get(""))
                    } catch (var8: IllegalArgumentException) {
                        var8.printStackTrace()
                    } catch (var9: IllegalAccessException) {
                        var9.printStackTrace()
                    }
                }

    }

    /**
     * 获得错误日志
     */
    private fun getCrashLog(paramThrowable: Throwable): String {
        val mStringBuffer = StringBuffer()
        val mPrintWriter = this.mLogInfo.entries.iterator()
        var mResult: String
        while (mPrintWriter.hasNext()) {
            val mWriter = mPrintWriter.next()
            val mThrowable = mWriter.key
            mResult = mWriter.value
            mStringBuffer.append(mThrowable + "=" + mResult + "\r\n")
        }
        val mWriter1 = StringWriter()
        val mPrintWriter1 = PrintWriter(mWriter1)
        paramThrowable.printStackTrace(mPrintWriter1)
        paramThrowable.printStackTrace()
        var mThrowable1: Throwable? = paramThrowable.cause
        while (mThrowable1 != null) {
            mThrowable1.printStackTrace(mPrintWriter1)
            mPrintWriter1.append("\r\n")
            mThrowable1 = mThrowable1.cause
        }
        mPrintWriter1.close()
        mResult = mWriter1.toString()
        mStringBuffer.append(mResult)
        Log.e("---------", mStringBuffer.toString())
        return mStringBuffer.toString()
    }

    companion object {
        private val TAG = "CrashLog"
        @SuppressLint("StaticFieldLeak")
        @JvmStatic val Instance = CrashLog()
    }
}

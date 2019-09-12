package com.linxiao.framework.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import com.blankj.utilcode.util.LogUtils
import com.linxiao.framework.notification.NotificationManager

/**
 * 启动Activity基类
 *
 * 执行App启动的预处理，此处用于执行框架模块的预处理操作。
 *
 * Created by linxiao on 2016/12/5.
 */
abstract class BaseSplashActivity : BaseActivity() {

    private var isHandleNotification: Boolean = false
    private var notificationExtra: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = intent
        notificationExtra = intent.getBundleExtra(NotificationManager.KEY_NOTIFICATION_EXTRA)
        isHandleNotification = notificationExtra != null

        if (!isTaskRoot) {
            finish()
            return
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handleNotification()
    }

    private fun handleNotification() {
        if (!isHandleNotification) {
            return
        }
        val targetKey = notificationExtra!!.getString(NotificationManager.KEY_TARGET_ACTIVITY_NAME)
        if (TextUtils.isEmpty(targetKey)) {
            LogUtils.e("BaseSplashActivity", "handleNotification: target key is null !")
            return
        }
        try {
            val destActivityClass = Class.forName(targetKey)
            val destIntent = Intent(this, destActivityClass)
            destIntent.putExtras(notificationExtra!!)
            startActivity(destIntent)
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
            LogUtils.e("BaseSplashActivity", "handleNotification: reflect to get activity class failed !")
        }
    }

}

package com.linxiao.framework.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.linxiao.framework.BaseApplication;

/**
 * 用于接收应用通知消息点击的事件并根据当前应用的状态做出处理
 * Created by LinXiao on 2016-12-05.
 */
public class NotificationReceiver extends BroadcastReceiver {

    private static final String TAG = NotificationReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        StringBuilder sb = new StringBuilder();
        sb.append("Receive Notification Broadcast").append("\n");
        Bundle notificationExtra = intent.getBundleExtra(NotificationManager.KEY_NOTIFICATION_EXTRA);
        if (notificationExtra == null) {
            sb.append("notification extra is null, can't guide to target page").append("\n");
            Log.d(TAG,sb.toString());
            return;
        }
        if (BaseApplication.isAppForeground()) {
            sb.append("application state: running").append("\n");
            Intent targetIntent = new Intent();
            String targetKey = notificationExtra.getString(NotificationManager.KEY_TARGET_ACTIVITY_NAME);
            if (TextUtils.isEmpty(targetKey)) {
                targetKey = NotificationResumeActivity.class.getName();
                sb.append("target key is empty, application will back to front as default").append("\n");
            }
            try {
                Class<?> targetActivityClass = Class.forName(targetKey);
                targetIntent.setClass(context, targetActivityClass);
                targetIntent.putExtras(notificationExtra);
            } catch (ClassNotFoundException e) {
                sb.append("cant find target activity class: ").append(targetKey).append(", application will back to front as default").append("\n");
                targetIntent.setClass(context, NotificationResumeActivity.class);
            }
            targetIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(targetIntent);
        }
        else {
            sb.append("application state: not running").append("\n");
            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(
                    BaseApplication.getAppContext().getPackageName());
            launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            launchIntent.putExtra(NotificationManager.KEY_NOTIFICATION_EXTRA, notificationExtra);
            context.startActivity(launchIntent);
        }
        Log.d(TAG,sb.toString());
    }

}

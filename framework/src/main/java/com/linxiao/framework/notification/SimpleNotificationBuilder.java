package com.linxiao.framework.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.List;

/**
 * 框架下常用通知构造类
 * <p>
 * 主要用于处理推送简易的文本，图片等信息，使用此类构建的Notification
 * 在没有设置点击事件时的操作为将App从后台唤起到前台，如果App未启动则启动App
 * </p>
 * Created by linxiao on 2016/12/8.
 */
public class SimpleNotificationBuilder {

    private static final String TAG = SimpleNotificationBuilder.class.getSimpleName();
    private static final String PUSH_CHANNEL_ID = "LG_PUSH_NOTIFY_ID";
    private static final String PUSH_CHANNEL_NAME = "LG_PUSH_NOTIFY_NAME";

    private Context mContext;
    private NotificationCompat.Builder mBuilder;
    private PendingIntent mPendingIntent;
    private boolean mHangUp = false;
    /**
     * 构造方法
     * <p>此处将Notification的icon设定为只用NotificationWrapper中所配置的默认icon</p>
     * */
    public SimpleNotificationBuilder(Context context, @NonNull String contentTitle, @NonNull String contentText) {
        this(context, NotificationManager.getDefaultIconRes(), contentTitle, contentText);
    }

    /**
     * 构造方法
     * <p>icon，contentTitle，contentText为构建一个Notification的必须参数，
     * 如果不传递这三个参数，代码不会报错，但通知不会显示</p>
     * */
    public SimpleNotificationBuilder(Context context, @DrawableRes int icon, @NonNull String contentTitle, @NonNull String contentText) {
        this.mContext = context;
        mBuilder = new NotificationCompat.Builder(context,PUSH_CHANNEL_ID);
        mBuilder.setSmallIcon(icon)
                .setContentTitle(contentTitle)
                .setContentText(contentText);
    }

    /**
     * 按照以下状态配置通知:<br>
     * priority : default <br>
     * when : current time <br>
     * autoCancel : true <br>
     * other options : default <br>
     * */
    public SimpleNotificationBuilder configureNotificationAsDefault() {
        mBuilder.setWhen(System.currentTimeMillis())
        .setDefaults(Notification.DEFAULT_VIBRATE)
        .setPriority(Notification.PRIORITY_DEFAULT)
        .setAutoCancel(true);
        return this;
    }

    /**
     * 向通知添加大内容文本
     * @param title 展开时的通知标题
     * @param contentText 展开时通知内容文本
     * @param summaryText 可以看作是副标题，不是很有用
     * */
    public SimpleNotificationBuilder setBigText(String title, String contentText, String summaryText) {
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle(title);
        bigTextStyle.bigText(contentText);
        if (!TextUtils.isEmpty(summaryText)) {
            bigTextStyle.setSummaryText(summaryText);
        }
        setStyle(bigTextStyle);
        return this;
    }
    /**
     * 向通知添加大内容文本
     * @param title 展开时的通知标题
     * @param contentText 展开时通知内容文本
     * */
    public SimpleNotificationBuilder setBigText(String title, String contentText) {
        setBigText(title, contentText, null);
        return this;
    }

    /**
     * 为通知添加大图内容
     * <p>由于Android的设定，图片高度最好不要超过256dp</p>
     * @param title 展开内容标题
     * @param picture 大图
     * @param summaryText 概要内容，可以看做二级标题，没啥用
     * */
    public SimpleNotificationBuilder setBigPicture(String title, Bitmap picture, String summaryText) {
        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.setBigContentTitle(title);
        bigPictureStyle.bigPicture(picture);
        if (!TextUtils.isEmpty(summaryText)) {
            bigPictureStyle.setSummaryText(summaryText);
        }
        setStyle(bigPictureStyle);
        return this;
    }
    /**
     * 为通知添加大图内容
     * <p>由于Android的设定，图片高度最好不要超过256dp</p>
     * @param title 展开内容标题
     * @param picture 大图
     * */
    public SimpleNotificationBuilder setBigPicture(String title, Bitmap picture) {
        setBigPicture(title, picture, null);
        return this;
    }

    /**
     * 为通知设置多行消息内容
     * @param title 展开内容标题
     * @param summaryText 概要内容
     * @param lines 多行文本
     * */
    public SimpleNotificationBuilder setInboxMessages(String title, String summaryText, List<String> lines) {
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(title);
        if (!TextUtils.isEmpty(summaryText)) {
            inboxStyle.setSummaryText(summaryText);
        }
        for (String line : lines) {
            inboxStyle.addLine(line);
        }
        setStyle(inboxStyle);
        return this;
    }
    /**
     * 为通知设置多行消息内容
     * @param title 展开内容标题
     * @param lines 多行文本
     * */
    public SimpleNotificationBuilder setInboxMessages(String title, List<String> lines) {
        setInboxMessages(title, null, lines);
        return this;
    }

    /**
     * 设置点击Notification时目标Activity的Intent
     * <p>默认处理逻辑为如果App的状态为运行中则直接打开目标Activity，如果App在后台则将App切换到前台
     * 如果App没有启动，则先启动App再打开目标Activity</p>
     * */
    public SimpleNotificationBuilder setTargetActivityIntent(Intent targetActivityIntent) {
        Intent broadcastIntent = new Intent(mContext, NotificationReceiver.class);
        Bundle bundle = new Bundle();
        if (targetActivityIntent.getExtras() != null) {
            bundle.putAll(targetActivityIntent.getExtras());
        }
        bundle.putString(NotificationManager.KEY_TARGET_ACTIVITY_NAME, targetActivityIntent.getComponent().getClassName());
        broadcastIntent.putExtra(NotificationManager.KEY_NOTIFICATION_EXTRA, bundle);
        mPendingIntent = PendingIntent.getBroadcast(mContext, 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        return this;
    }

    /**
     * 设置点击Notification时目标Activity的Intent，并在打开时将其上一级Activity添加到回退栈
     * <p><strong>使用此方法打开Activity时，将清空当前App的TaskStack,无论App当时是否处于运行状态</strong>
     * <br>
     * <strong>注意：目标Activity要想在back时成功回退到指定的Activity必须在该Activity的manifest声明中添加
     *      <br>android:parentActivityName="回退Activity路径"<br>
     *  的属性，否则将不会生效
     * </strong></p>
     * */
    public SimpleNotificationBuilder setTargetActivityWithParentStack(Intent targetActivityIntent) {
        String className = targetActivityIntent.getComponent().getClassName();
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        try {
            Class<?> sourceActivityClass = Class.forName(className);
            stackBuilder.addParentStack(sourceActivityClass);
            Log.d(TAG, "setDestWithParentStack: destActivityClass = " + sourceActivityClass.getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        stackBuilder.addNextIntent(targetActivityIntent);
        mPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        return this;
    }

    /**
     * 不走封装Builder类默认的处理逻辑自定义PendingIntent时使用
     * */
    public SimpleNotificationBuilder setCustomPendingIntent(PendingIntent pendingIntent) {
        mPendingIntent = pendingIntent;
        return this;
    }

    /**
     * 显示横幅通知，在 Android 5.0 以下不会生效
     * */
    public SimpleNotificationBuilder setHangUp(boolean show) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mHangUp = false;
            return this;
        }
        mHangUp = show;
        return this;
    }

    /**
     * 构建Notification类
     *
     * @return built Notification
     * */
    public Notification build() {
        if (mPendingIntent == null) {
            Intent destIntent = new Intent(mContext, NotificationReceiver.class);
            mPendingIntent = PendingIntent.getBroadcast(mContext, 0, destIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        if (mHangUp) {
            //这句是重点
            mBuilder.setFullScreenIntent(mPendingIntent, true);
            mBuilder.setAutoCancel(true);
        }
        mBuilder.setContentIntent(mPendingIntent);

        return mBuilder.build();
    }
    
    /**
     * 构建通知并发送
     *
     * */
    public void send(int notifyId) {
        android.app.NotificationManager notificationManager = (android.app.NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(PUSH_CHANNEL_ID, PUSH_CHANNEL_NAME, android.app.NotificationManager.IMPORTANCE_MIN);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
//        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        if (notificationManager != null) {
            notificationManager.notify(notifyId, build());
        }
//        NotificationManagerCompat.from(mContext).notify(notifyId, build());
    }

    public Context getBuilderContext() {
        return mContext;
    }


    /*-----以下为代理NotificationCompat.Builder的常用方法，方便快速配置Notification-----*/

    /**
     * 可能不会在一些低api机器上显示
     * */
    public SimpleNotificationBuilder setSubText(String subText) {
        mBuilder.setSubText(subText);
        return this;
    }
    /**
     * 看起来不会对高于棒棒糖的系统产生影响
     * */
    public SimpleNotificationBuilder setTicker(String tickerText) {
        mBuilder.setTicker(tickerText);
        return this;
    }

    /**
     * 如果调用setContentInfo()，该方法将不生效
     * */
    public SimpleNotificationBuilder setNumber(int number) {
        mBuilder.setNumber(number);
        return this;
    }
    
    public SimpleNotificationBuilder setWhen(long when) {
        mBuilder.setWhen(when);
        return this;
    }
    
    public SimpleNotificationBuilder setShowWhen(boolean show) {
        mBuilder.setShowWhen(show);
        return this;
    }
    
    public SimpleNotificationBuilder setUsesChronometer(boolean b) {
        mBuilder.setUsesChronometer(b);
        return this;
    }
    
    public SimpleNotificationBuilder setSmallIcon(int icon) {
        mBuilder.setSmallIcon(icon);
        return this;
    }
    
    public SimpleNotificationBuilder setSmallIcon(int icon, int level) {
        mBuilder.setSmallIcon(icon, level);
        return this;
    }

    public SimpleNotificationBuilder setContentTitle(CharSequence title) {
        mBuilder.setContentTitle(title);
        return this;
    }
    
    public SimpleNotificationBuilder setContentText(CharSequence text) {
        mBuilder.setContentText(text);
        return this;
    }
    
    public SimpleNotificationBuilder setSubText(CharSequence text) {
        mBuilder.setSubText(text);
        return this;
    }
    
    public SimpleNotificationBuilder setRemoteInputHistory(CharSequence[] text) {
        mBuilder.setRemoteInputHistory(text);
        return this;
    }
    
    public SimpleNotificationBuilder setContentInfo(CharSequence info) {
        mBuilder.setContentInfo(info);
        return this;
    }
    
    public SimpleNotificationBuilder setProgress(int max, int progress, boolean indeterminate) {
        mBuilder.setProgress(max, progress, indeterminate);
        return this;
    }
    
    public SimpleNotificationBuilder setContent(RemoteViews views) {
        mBuilder.setContent(views);
        return this;
    }
    
    public SimpleNotificationBuilder setContentIntent(PendingIntent intent) {
        mBuilder.setContentIntent(intent);
        return this;
    }
    
    public SimpleNotificationBuilder setDeleteIntent(PendingIntent intent) {
        mBuilder.setDeleteIntent(intent);
        return this;
    }
    
    public SimpleNotificationBuilder setFullScreenIntent(PendingIntent intent, boolean highPriority) {
        mBuilder.setFullScreenIntent(intent, highPriority);
        return this;
    }
    
    public SimpleNotificationBuilder setTicker(CharSequence tickerText) {
        mBuilder.setTicker(tickerText);
        return this;
    }
    
    public SimpleNotificationBuilder setTicker(CharSequence tickerText, RemoteViews views) {
        mBuilder.setTicker(tickerText, views);
        return this;
    }
    
    public SimpleNotificationBuilder setLargeIcon(Bitmap icon) {
        mBuilder.setLargeIcon(icon);
        return this;
    }
    
    public SimpleNotificationBuilder setSound(Uri sound) {
        mBuilder.setSound(sound);
        return this;
    }
    
    public SimpleNotificationBuilder setSound(Uri sound, int streamType) {
        mBuilder.setSound(sound, streamType);
        return this;
    }
    
    public SimpleNotificationBuilder setVibrate(long[] pattern) {
        mBuilder.setVibrate(pattern);
        return this;
    }
    
    public SimpleNotificationBuilder setLights(@ColorInt int argb, int onMs, int offMs) {
        mBuilder.setLights(argb, onMs, offMs);
        return this;
    }
    
    public SimpleNotificationBuilder setOngoing(boolean ongoing) {
        mBuilder.setOngoing(ongoing);
        return this;
    }
    
    public SimpleNotificationBuilder setOnlyAlertOnce(boolean onlyAlertOnce) {
        mBuilder.setOnlyAlertOnce(onlyAlertOnce);
        return this;
    }
    
    public SimpleNotificationBuilder setAutoCancel(boolean autoCancel) {
        mBuilder.setAutoCancel(autoCancel);
        return this;
    }
    
    public SimpleNotificationBuilder setLocalOnly(boolean b) {
        mBuilder.setLocalOnly(b);
        return this;
    }
    
    public SimpleNotificationBuilder setCategory(String category) {
        mBuilder.setCategory(category);
        return this;
    }
    
    public SimpleNotificationBuilder setDefaults(int defaults) {
        mBuilder.setDefaults(defaults);
        return this;
    }
    
    public SimpleNotificationBuilder setPriority(int pri) {
        mBuilder.setPriority(pri);
        return this;
    }
    
    public SimpleNotificationBuilder addPerson(String uri) {
        mBuilder.addPerson(uri);
        return this;
    }

    public SimpleNotificationBuilder setGroup(String groupKey) {
        mBuilder.setGroup(groupKey);
        return this;
    }

    public SimpleNotificationBuilder setGroupSummary(boolean isGroupSummary) {
        mBuilder.setGroupSummary(isGroupSummary);
        return this;
    }
    
    public SimpleNotificationBuilder setSortKey(String sortKey) {
        mBuilder.setSortKey(sortKey);
        return this;
    }
    
    public SimpleNotificationBuilder addExtras(Bundle extras) {
        mBuilder.addExtras(extras);
        return this;
    }
    
    public SimpleNotificationBuilder setExtras(Bundle extras) {
        mBuilder.setExtras(extras);
        return this;
    }
    
    public Bundle getExtras() {
        return mBuilder.getExtras();
    }
    
    public SimpleNotificationBuilder addAction(int icon, CharSequence title, PendingIntent intent) {
        mBuilder.addAction(icon, title, intent);
        return this;
    }
    
    public SimpleNotificationBuilder addAction(NotificationCompat.Action action) {
        mBuilder.addAction(action);
        return this;
    }
    
    public SimpleNotificationBuilder setStyle(NotificationCompat.Style style) {
        mBuilder.setStyle(style);
        return this;
    }
    
    public SimpleNotificationBuilder setColor(@ColorInt int argb) {
        mBuilder.setColor(argb);
        return this;
    }
    
    public SimpleNotificationBuilder setVisibility(@NotificationCompat.NotificationVisibility int visibility) {
        mBuilder.setVisibility(visibility);
        return this;
    }

    public SimpleNotificationBuilder setPublicVersion(Notification n) {
        mBuilder.setPublicVersion(n);
        return this;
    }
    
    public SimpleNotificationBuilder setCustomContentView(RemoteViews contentView) {
        mBuilder.setCustomContentView(contentView);
        return this;
    }
    
    public SimpleNotificationBuilder setCustomBigContentView(RemoteViews contentView) {
        mBuilder.setCustomBigContentView(contentView);
        return this;
    }

    public SimpleNotificationBuilder setCustomHeadsUpContentView(RemoteViews contentView) {
        mBuilder.setCustomHeadsUpContentView(contentView);
        return this;
    }
    
    public SimpleNotificationBuilder extend(NotificationCompat.Extender extender) {
        mBuilder.extend(extender);
        return this;
    }
}

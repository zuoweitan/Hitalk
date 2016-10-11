package com.zuowei.utils.common;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.avos.avospush.notification.NotificationCompat;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by zuowei on 16-7-22.
 */
public class NotificationUtils {
    private static final int REPLY_NOTIFY_ID = 1;
    private static List<String> notificationTagList = new LinkedList();

    private NotificationUtils() {
    }

    public static void addTag(String tag) {
        if(!notificationTagList.contains(tag)) {
            notificationTagList.add(tag);
        }

    }

    public static void removeTag(String tag) {
        notificationTagList.remove(tag);
    }

    public static boolean isShowNotification(String tag) {
        return !notificationTagList.contains(tag);
    }

    public static void showNotification(Context context, String title, String content, Intent intent) {
        showNotification(context, title, content, (String)null, intent);
    }

    public static void showNotification(Context context, String title, String content, String sound, Intent intent) {
        PendingIntent contentIntent = PendingIntent.getActivity(context, 1, intent, 0);
        NotificationCompat.Builder mBuilder = (new NotificationCompat.Builder(context)).setSmallIcon(context.getApplicationInfo().icon).setContentTitle(title).setAutoCancel(true).setContentIntent(contentIntent).setDefaults(3).setContentText(content);
        NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = mBuilder.build();
        if(sound != null && sound.trim().length() > 0) {
            notification.sound = Uri.parse("android.resource://" + sound);
        }

        manager.notify(1, notification);
    }

    public static void cancelNotification(Context context) {
        NotificationManager nMgr = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancel(1);
    }
}

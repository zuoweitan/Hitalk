package com.zuowei.utils.common;

import android.app.ActivityManager;
import android.content.Context;

/**
 * Created by zuowei on 16-7-13.
 */
public class ApplicationUtils {
    //get app process name
    public static String getCurrentProcessName (Context context){
        int pid = android.os.Process.myPid();
        return getProcessName(context,pid);
    }

    public static String getProcessName (Context context,int pid){
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }
}

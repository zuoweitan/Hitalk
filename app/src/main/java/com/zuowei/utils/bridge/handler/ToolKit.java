package com.zuowei.utils.bridge.handler;

import android.os.AsyncTask;
import android.os.Looper;

/**
 * 一个用于子线程跳转回主线程的实现
 *
 * 1.异步进入主线程,无需等待 {@link #runOnMainThreadAsync(Runnable)}
 * 2.同步进入主线程,等待主线程处理完成后继续执行子线程 {@link #runOnMainThreadSync(Runnable)}
 *
 * Created by livvy on 3/5/16.
 */
public class ToolKit {

    private static HandlerPoster mainPoster = null;

    private static HandlerPoster getMainPoster() {
        if (mainPoster == null) {
            synchronized (ToolKit.class) {
                if (mainPoster == null) {
                    mainPoster = new HandlerPoster(Looper.getMainLooper(), 20);
                }
            }
        }
        return mainPoster;
    }

    public static void runOnBackgroundThreadPool(Runnable runnable){
        AsyncTask.THREAD_POOL_EXECUTOR.execute(runnable);
    }

    //异步进入主线程,无需等待
    public static void runOnMainThreadAsync(Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
            return;
        }
        getMainPoster().async(runnable);
    }

    //同步进入主线程,等待主线程处理完成后继续执行子线程
    public static void runOnMainThreadSync(Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
            return;
        }
        SyncPost poster = new SyncPost(runnable);
        getMainPoster().sync(poster);
        poster.waitRun();
    }

    public static void dispose() {
        if (mainPoster != null) {
            mainPoster.dispose();
            mainPoster = null;
        }
    }
}

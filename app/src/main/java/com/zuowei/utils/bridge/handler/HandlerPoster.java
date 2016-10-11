package com.zuowei.utils.bridge.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;

import java.util.LinkedList;
import java.util.Queue;

/**
 * handler的实现:
 * 两个标识，两个队列，两个执行状态，一个时间限制；
 * 标识为了区别分别是处理那个队列使用;队列当然是装着任务了;执行状态是为了避免重复发送消息导致消息队列过多;时间限制不多说
 * <p>
 * Created by livvy on 3/5/16.
 */
final class HandlerPoster extends Handler {

    private final int ASYNC = 0x1;
    private final int SYNC = 0x2;

    private final Queue<Runnable> asyncPool;
    private final Queue<SyncPost> syncPool;

    private final int maxMillisInsideHandleMessage;
    private boolean asyncActive;
    private boolean syncActive;

    //传入两个参数，分别是 Looper，用于初始化到主线程，后面的是时间限制；然后初始化了两个队列。
    HandlerPoster(Looper looper, int maxMillisInsideHandleMessage) {
        super(looper);
        this.maxMillisInsideHandleMessage = maxMillisInsideHandleMessage;
        asyncPool = new LinkedList<>();
        syncPool = new LinkedList<>();
    }

    //去除掉没有处理的消息，然后清空队列
    void dispose() {
        this.removeCallbacksAndMessages(null);
        this.asyncPool.clear();
        this.syncPool.clear();
    }

    //进入同步状态，然后调用asyncPool.offer(runnable);把任务写入到队列
    void async(Runnable runnable) {
        synchronized (asyncPool) {
            asyncPool.offer(runnable);
            //判断当前是否处于异步任务执行中，如果不是：立刻改变状态，然后发送一个消息给当前
            if (!asyncActive) {
                asyncActive = true;
                if (!sendMessage(obtainMessage(ASYNC))) {
                    throw new HandlerException("Could not send handler message");
                }
            }
        }
    }

    void sync(SyncPost post) {
        synchronized (syncPool) {
            syncPool.offer(post);
            if (!syncActive) {
                syncActive = true;
                if (!sendMessage(obtainMessage(SYNC))) {
                    throw new HandlerException("Could not send handler message");
                }
            }
        }
    }

    //复写的Handler的消息处理方法
    @Override
    public void handleMessage(Message msg) {
        //判断是否是进行异步处理的消息，如果是那么进入该位置
        if (msg.what == ASYNC) {
            boolean rescheduled = false;
            //进行了try_finally有一个变量long_started用于标识开始时间
            try {
                long started = SystemClock.uptimeMillis();
                while (true) {
                    //队列取出一个任务，采用Poll方法；判断是否为空，如果为空进入队列同步块
                    Runnable runnable = asyncPool.poll();
                    if (runnable == null) {
                        synchronized (asyncPool) {
                            //再取一次，再次如果恰巧在进入同步队列之前有新的任务来了，那么第二次取到的当然就不是 NULL也就会继续执行下去
                            runnable = asyncPool.poll();
                            if (runnable == null) {
                                ////如果还是为空；那么重置当前队列的状态为false同时跳出循环
                                asyncActive = false;
                                return;
                            }
                        }
                    }
                    runnable.run();
                    long timeInMethod = SystemClock.uptimeMillis() - started;
                    //执行一个任务后就判断一次如果超过了每次占用主线程的时间限制，那么不管队列中的任务是否执行完成都退出
                    if (timeInMethod >= maxMillisInsideHandleMessage) {
                        if (!sendMessage(obtainMessage(ASYNC))) {
                            throw new HandlerException("Could not send handler message");
                        }
                        rescheduled = true;
                        return;
                    }
                }
            } finally {
                asyncActive = rescheduled;
            }
        } else if (msg.what == SYNC) {
            //如果是同步任务消息就进入，如果还是不是 那么只有调用super.handleMessage(msg);了
            boolean rescheduled = false;
            try {
                long started = SystemClock.uptimeMillis();
                while (true) {
                    SyncPost post = syncPool.poll();
                    if (post == null) {
                        synchronized (syncPool) {
                            post = syncPool.poll();
                            if (post == null) {
                                syncActive = false;
                                return;
                            }
                        }
                    }
                    post.run();
                    long timeInMethod = SystemClock.uptimeMillis() - started;
                    if (timeInMethod >= maxMillisInsideHandleMessage) {
                        if (!sendMessage(obtainMessage(SYNC))) {
                            throw new HandlerException("Could not send handler message");
                        }
                        rescheduled = true;
                        return;
                    }
                }
            } finally {
                syncActive = rescheduled;
            }
        } else super.handleMessage(msg);
    }
}

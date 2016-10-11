package com.zuowei.utils.downtime;

/**
 * Created by zuowei on 16-7-21.
 */
public interface DownTimerListener {
    void onTick(long millisUntilFinished);
    void onFinish();
}

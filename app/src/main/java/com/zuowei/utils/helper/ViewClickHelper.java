package com.zuowei.utils.helper;

import android.content.Context;
import android.view.View;

import com.vivifram.second.hitalk.HiTalkApplication;
import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.broadcast.ConnectivityNotifier;
import com.zuowei.utils.common.NToast;

import java.util.Calendar;

/**
 * 项目名称：Hitalk
 * 类描述：
 * 创建人：zuowei
 * 创建时间：16-12-14 下午8:02
 * 修改人：zuowei
 * 修改时间：16-12-14 下午8:02
 * 修改备注：
 */
public abstract class ViewClickHelper implements View.OnClickListener {
    public static final int MIN_CLICK_DELAY_TIME = 1000;
    private long lastClickTime = 0;
    @Override
    public void onClick(View v) {
        Context appContext = HiTalkApplication.$();
        if (appContext != null && ConnectivityNotifier.isConnected(appContext)){
            long currentTime = Calendar.getInstance().getTimeInMillis();
            if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
                lastClickTime = currentTime;
                onRealClick(v);
            } else if (currentTime <= lastClickTime){
                lastClickTime = currentTime;
                onRealClick(v);
            }
        }else {
            NToast.shortToast(appContext, R.string.network_unavailable);
        }
    }

    public abstract void onRealClick(View v);
}

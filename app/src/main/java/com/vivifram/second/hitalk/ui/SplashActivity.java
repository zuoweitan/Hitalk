package com.vivifram.second.hitalk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.base.BaseActivity;
import com.vivifram.second.hitalk.manager.chat.ClientManager;
import com.zuowei.utils.common.NToast;
import com.zuowei.utils.common.UIUtils;
import com.zuowei.utils.helper.HiTalkHelper;

/**
 * Created by zuowei on 16-7-14.
 */
public class SplashActivity extends BaseActivity {

    private static final int MAX_SLEEPTIME = 2000;
    private Handler mHandler;
    private static final int MSG_GO_MAIN = 0x01;
    private static final int MSG_GO_LOGIN = 0x02;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.splash);
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case MSG_GO_LOGIN:
                            goLogin();
                        break;
                    case MSG_GO_MAIN:
                            goMain();
                        break;
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        new Thread(() -> {
            if (HiTalkHelper.getInstance().isLoggedIn()) {

                HiTalkHelper.getInstance().updateUserInfo();
                tryToOpenClient();
                mHandler.sendEmptyMessageDelayed(MSG_GO_MAIN,MAX_SLEEPTIME);
            }else {
                goLogin();
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }

    private void goLogin() {
        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        finish();
    }

    private void goMain() {
        if (HiTalkHelper.getInstance().isInfoFull()) {
            UIUtils.startActivitySafety(this,HiTalkActivity.class);
        }else {
            NToast.shortToast(mAppCtx,getString(R.string.not_fill_info_warn));
            UIUtils.startActivitySafety(this,FillInfoActivity.class);
        }
        finish();
    }

    private void tryToOpenClient() {
        ClientManager.getInstance().open(HiTalkHelper.getInstance().getCurrentUserId(), new AVIMClientCallback() {
            @Override
            public void done(AVIMClient avimClient, AVIMException e) {
                mHandler.removeMessages(MSG_GO_MAIN);
                mHandler.sendEmptyMessage(MSG_GO_MAIN);
            }
        });
    }
}

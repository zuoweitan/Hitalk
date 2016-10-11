package com.vivifram.second.hitalk.ui;

import android.os.Bundle;
import android.os.Handler;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.base.BaseActivity;
import com.vivifram.second.hitalk.base.LayoutInject;
import com.vivifram.second.hitalk.bean.UserWrap;
import com.vivifram.second.hitalk.manager.chat.ClientManager;
import com.vivifram.second.hitalk.ui.layout.BaseLayout;
import com.vivifram.second.hitalk.ui.layout.FillInfoLayout;
import com.zuowei.utils.bridge.EaterManager;
import com.zuowei.utils.bridge.constant.EaterAction;
import com.zuowei.utils.common.UIUtils;
import com.zuowei.utils.handlers.LoginHandler;
import com.zuowei.utils.helper.HiTalkHelper;
import com.zuowei.utils.helper.UserCacheHelper;

/**
 * Created by zuowei on 16-7-26.
 */
@LayoutInject(name = "FillInfoLayout")
public class FillInfoActivity extends BaseActivity<FillInfoLayout> implements
        BaseLayout.OnLayoutEventListener<FillInfoLayout> {
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        EaterManager.getInstance().registerEater(EaterAction.ACTION_DO_LOGIN, LoginHandler.getInstance());
        setContentView(R.layout.activity_fillinfo_layout);
        mLayout.setOnLayoutEventListener(this);
    }

    @Override
    public void onContentConfirmed(FillInfoLayout fillInfoLayout) {
        UserCacheHelper.getInstance().saveUserToCloud(new UserWrap()
                .putNickName(fillInfoLayout.getNickName())
                .putCollege(fillInfoLayout.getCollege())
                .putSex(fillInfoLayout.getSex())
                .putInterest(fillInfoLayout.getInterests()));
        tryToOpenClient();
    }

    private void tryToOpenClient() {
        ClientManager.getInstance().open(HiTalkHelper.getInstance().getCurrentUserId(), new AVIMClientCallback() {
            @Override
            public void done(AVIMClient avimClient, AVIMException e) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        UIUtils.startActivitySafety(FillInfoActivity.this,HiTalkActivity.class);
                        finish();
                    }
                },0);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EaterManager.getInstance().unRegisterEater(LoginHandler.getInstance());
    }
}

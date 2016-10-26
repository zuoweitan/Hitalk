package com.vivifram.second.hitalk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.base.BaseActivity;
import com.vivifram.second.hitalk.base.LayoutInject;
import com.vivifram.second.hitalk.manager.chat.ClientManager;
import com.vivifram.second.hitalk.ui.layout.LoginLayout;
import com.zuowei.utils.bridge.EaterManager;
import com.zuowei.utils.bridge.constant.EaterAction;
import com.zuowei.utils.bridge.params.LoginParam;
import com.zuowei.utils.common.UIUtils;
import com.zuowei.utils.handlers.LoginHandler;
import com.zuowei.utils.helper.HiTalkHelper;

/**
 * Created by zuowei on 16-7-18.
 */
@LayoutInject(name = "LoginLayout")
public class LoginActivity extends BaseActivity<LoginLayout> implements LoginHandler.OnLoginListener,
        View.OnClickListener{
    LoginHandler mLoginHandler;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_login_layout);
        mLoginHandler = LoginHandler.getInstance();
        mLayout.setOnLoginButtonClick(this);
        mLayout.setRegisterOnClick(v -> goRegister());
        mLoginHandler.addLoginListener(this);
        EaterManager.getInstance().registerEater(EaterAction.ACTION_DO_LOGIN,mLoginHandler);
    }

    private void goRegister() {
        startActivityForResult(new Intent(this, RegisterActivity.class),0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == 1){
            String username = data.getStringExtra("username");
            String password = data.getStringExtra("password");
            mLayout.setUserName(username);
            mLayout.setPassword(password);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLoginHandler.removeLoginListener(this);
        EaterManager.getInstance().unRegisterEater(mLoginHandler);
    }

    @Override
    public void onSuccess() {
        tryToOpenClient();
    }

    private void tryToOpenClient() {
        ClientManager.getInstance().open(HiTalkHelper.getInstance().getCurrentUserId(), new AVIMClientCallback() {
            @Override
            public void done(AVIMClient avimClient, AVIMException e) {
                new Handler().postDelayed(() -> {
                    UIUtils.startActivitySafety(LoginActivity.this,HiTalkActivity.class);
                    finish();
                },0);
            }
        });
    }

    @Override
    public void onError(int code, final String message) {
        runOnUiThread(() -> {
            Toast.makeText(LoginActivity.this,message,Toast.LENGTH_SHORT).show();
            mLayout.resetLoginButton();
        });
    }

    @Override
    public void onClick(View v) {
        EaterManager.getInstance().broadcast(
                new LoginParam.Builder(LoginParam.TYPE_LOGIN).setUserName(mLayout.getUserName())
                    .setPassword(mLayout.getPassword()).create());
    }
}

package com.vivifram.second.hitalk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.base.BaseActivity;
import com.vivifram.second.hitalk.base.LayoutInject;
import com.vivifram.second.hitalk.ui.layout.RegisterLayout;
import com.zuowei.utils.bridge.EaterManager;
import com.zuowei.utils.bridge.constant.EaterAction;
import com.zuowei.utils.bridge.params.LoginParam;
import com.zuowei.utils.common.NToast;
import com.zuowei.utils.common.UIUtils;
import com.zuowei.utils.handlers.LoginHandler;

/**
 * Created by zuowei on 16-7-21.
 */
@LayoutInject(name = "RegisterLayout")
public class RegisterActivity extends BaseActivity<RegisterLayout> implements View.OnClickListener
        , LoginHandler.OnVerifyListener ,LoginHandler.OnRegisterListener{
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_register_layout);
        mLayout.setRegisterButtonOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doRegister();
            }
        });
        EaterManager.getInstance().registerEater(EaterAction.ACTION_DO_LOGIN,LoginHandler.getInstance());
        LoginHandler.getInstance().addVerifyListener(this);
        LoginHandler.getInstance().addRegisterListener(this);
    }

    private void doRegister() {
        EaterManager.getInstance().broadcast(
                new LoginParam.Builder(LoginParam.TYPE_VERIFY)
                        .setCode(mLayout.getCode())
                        .setPhoneNumber(mLayout.getPhoneNumber())
                        .create());
       /* UIUtils.startActivitySafety(this,new Intent(this,FillInfoActivity.class));
        finish();*/
    }

    @Override
    public void onClick(View v) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LoginHandler.getInstance().removeVerifyListener(this);
        EaterManager.getInstance().unRegisterEater(LoginHandler.getInstance());
        LoginHandler.getInstance().removeRegisterListener(this);
    }

    @Override
    public void onRegisterSuccess() {
        notifyActivityDestroy(LoginActivity.class);
        UIUtils.startActivitySafety(this,FillInfoActivity.class);
        finish();
    }

    @Override
    public void onRegisterError(int code, final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                NToast.shortToast(RegisterActivity.this,getString(R.string.registerFailed) + message);
                mLayout.resetRegisterButton();
            }
        });
    }

    @Override
    public void onVerifySuccess() {
        EaterManager.getInstance().broadcast(new LoginParam.Builder(LoginParam.TYPE_REGISTER)
                .setUserName(mLayout.getPhoneNumber())
                .setPassword(mLayout.getPassWord())
                .setPhoneNumber(mLayout.getPhoneNumber())
                .create());
    }

    @Override
    public void onVerifyError(int code, String message) {
        NToast.shortToast(mAppCtx,getString(R.string.code_is_wrong));
        mLayout.resetRegisterButton();
    }

}

package com.vivifram.second.hitalk.ui.layout;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.ui.view.ShrinkButton;

/**
 * Created by zuowei on 16-7-18.
 */
public class LoginLayout extends BaseLayout implements View.OnClickListener{

    private EditText mUnEdt;
    private EditText mPwEdt;
    private ShrinkButton mLgBtn;
    private TextView mRegTv;
    private View.OnClickListener mRegisterOnClick;

    public LoginLayout(View rootView) {
        super(rootView);
    }

    @Override
    public void onContentViewCreate(View view) {
        super.onContentViewCreate(view);
        init();
    }

    private void init() {
        mUnEdt = (EditText) findViewById(R.id.etUserName);
        mPwEdt = (EditText) findViewById(R.id.etPassword);
        mLgBtn = (ShrinkButton) findViewById(R.id.btnSignIn);
        mRegTv = (TextView) findViewById(R.id.registerTv);
        mRegTv.setOnClickListener(this);
    }

    public void setOnLoginButtonClick(final View.OnClickListener onClickListener){
        mLgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(getUserName())||
                        TextUtils.isEmpty(getPassword())){
                    Toast.makeText(mCtx,mRes.getString(R.string.login_warn),Toast.LENGTH_SHORT).show();
                    resetLoginButton();
                    return;
                }
                onClickListener.onClick(v);
            }
        });
    }

    public String getUserName(){
        return mUnEdt.getText()+"";
    }

    public String getPassword(){
        return mPwEdt.getText()+"";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.registerTv:
                    if (mRegisterOnClick != null){
                        mRegisterOnClick.onClick(v);
                    }
                break;
        }
    }

    public void resetLoginButton() {
        mLgBtn.reset();
    }

    public void setRegisterOnClick(View.OnClickListener registerOnClick) {
        this.mRegisterOnClick = registerOnClick;
    }

    public void setUserName(String username) {
        mUnEdt.setText(username);
    }

    public void setPassword(String password){
        mPwEdt.setText(password);
    }
}

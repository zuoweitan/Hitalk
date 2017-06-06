package com.vivifram.second.hitalk.ui.layout;

import android.animation.ValueAnimator;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.ui.view.FlashBorderView;
import com.vivifram.second.hitalk.ui.view.ShrinkButton;
import com.zuowei.utils.common.DisplayUtil;
import com.zuowei.utils.common.NToast;
import com.zuowei.utils.common.TagUtil;
import com.zuowei.utils.common.Utils;
import com.zuowei.utils.downtime.DownTimer;
import com.zuowei.utils.downtime.DownTimerListener;
import com.zuowei.utils.handlers.LoginHandler;

/**
 * Created by zuowei on 16-7-21.
 */
public class RegisterLayout extends BaseLayout implements DownTimerListener,View.OnClickListener,
        LoginHandler.OnRequestVerifyListener{

    private static final String TAG = TagUtil.makeTag(RegisterLayout.class);
    private EditText mPhoneNumberEt;
    private EditText mPassdEt;
    private EditText mVerifyEt;
    private TextView mCodeTipsTv;
    private FlashBorderView mSendCodeBtn;
    private ShrinkButton mRegisterBtn;

    private ValueAnimator mValueAnimator;
    private View.OnClickListener mSendCodeListener;
    private View.OnClickListener mRegisterButtonOnclickListener;
    private DownTimer mDownTimer;
    private boolean mRequestSuccess;

    public RegisterLayout(View rootView) {
        super(rootView);
    }

    @Override
    public void onContentViewCreate(View view) {
        super.onContentViewCreate(view);
        init();
    }

    @Override
    public void onWindowAttached() {
        super.onWindowAttached();
        LoginHandler.getInstance().addRequestVerifyListener(this);
    }

    @Override
    public void onWindowDetached() {
        super.onWindowDetached();
        LoginHandler.getInstance().removeRequestVerifyListener(this);
    }

    private void init() {
        mPhoneNumberEt = (EditText) findViewById(R.id.etPhoneNumber);
        mVerifyEt = (EditText) findViewById(R.id.etVerifyCode);
        mPassdEt = (EditText) findViewById(R.id.etPassword);
        mSendCodeBtn = (FlashBorderView) findViewById(R.id.btGetCode);
        mRegisterBtn = (ShrinkButton) findViewById(R.id.btRegister);
        mCodeTipsTv = (TextView) findViewById(R.id.tvCodeTips);

        initPassHint();

        mPhoneNumberEt.addTextChangedListener(new EdittextWatcher(){
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                if (s.length() == 11) {
                    if (Utils.isMobile(s.toString().trim())) {
                        Utils.onInactive(mCtx,mPhoneNumberEt);
                    } else {
                        NToast.shortToast(mCtx,R.string.illegal_phone_number);
                    }
                }
            }
        });

        mVerifyEt.addTextChangedListener(new EdittextWatcher(){
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 6) {
                    Utils.onInactive(mCtx, mVerifyEt);
                }
            }
        });

        mPassdEt.addTextChangedListener(new EdittextWatcher(){
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                if (s.length() > 4) {
                    mRegisterBtn.setClickable(true);
                } else {
                    mRegisterBtn.setClickable(false);
                }
            }
        });

        initAnimators();
        initListeners();
        mSendCodeBtn.setOnClickListener(mSendCodeListener);
        mRegisterBtn.setOnClickListener(this);
    }

    private void initPassHint() {
        String hintPart1 = mRes.getString(R.string.passwordRegTips);
        String hintPart2 = mRes.getString(R.string.passwordRegSubTips);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        SpannableString sPart1 = new SpannableString(hintPart1);
        SpannableString sPart2 = new SpannableString(hintPart2);
        AbsoluteSizeSpan span = new AbsoluteSizeSpan(DisplayUtil.dip2px(mAppCtx,12));
        sPart2.setSpan(span,0,hintPart2.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.append(sPart1).append(sPart2);
        mPassdEt.setHint(spannableStringBuilder);
    }

    private void initListeners() {
        mSendCodeListener = v -> {
            if (checkPhoneAndPassword()) {
                mValueAnimator.start();
                if (mDownTimer != null) {
                    mDownTimer.stopDown();
                }
                mDownTimer = new DownTimer();
                mDownTimer.setListener(RegisterLayout.this);
                mDownTimer.startDown(60 * 1000, 1000);
                mSendCodeBtn.setOnClickListener(null);
                sendCode();
            }
        };
    }

    private void sendCode() {
        //you can uncomment this to use the function
        /*EaterManager.getInstance().broadcast(
                new LoginParam.Builder(LoginParam.TYPE_REQUEST_VERIFY)
                        .setPhoneNumber(getPhoneNumber())
                        .create());*/
        NToast.shortToast(mAppCtx, mRes.getString(R.string.comment_function));
    }

    private void initAnimators() {
        mValueAnimator = ValueAnimator.ofFloat(0,1);
        mValueAnimator.setDuration(700);
        mValueAnimator.setRepeatMode(ValueAnimator.RESTART);
        mValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mValueAnimator.addUpdateListener(animation -> {
            mSendCodeBtn.setmFraction(animation.getAnimatedFraction());
            mSendCodeBtn.postInvalidate();
        });
    }

    public String getPhoneNumber(){
        return mPhoneNumberEt.getText()+"";
    }

    public String getPassWord(){
        return mPassdEt.getText()+"";
    }

    public String getCode(){
        return mVerifyEt.getText()+"";
    }

    @Override
    public void onTick(long millisUntilFinished) {
        mCodeTipsTv.setText("" + String.valueOf(millisUntilFinished / 1000));
    }

    @Override
    public void onFinish() {
        mValueAnimator.cancel();
        mSendCodeBtn.setmFraction(0f);
        mSendCodeBtn.postInvalidate();
        mSendCodeBtn.setOnClickListener(mSendCodeListener);
        mCodeTipsTv.setText(mRes.getString(R.string.send_code));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btRegister:
                    if (checkAllParams()){
                        if (mRegisterButtonOnclickListener != null){
                            mRegisterButtonOnclickListener.onClick(v);
                        }
                    }else {
                        mRegisterBtn.reset();
                    }
                break;
        }
    }

    private boolean checkPhoneAndPassword(){
        String phone = mPhoneNumberEt.getText().toString().trim();
        String password = mPassdEt.getText().toString().trim();

        if (TextUtils.isEmpty(phone)) {
            NToast.shortToast(mCtx, mRes.getString(R.string.phone_number_is_null));
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            NToast.shortToast(mCtx, mRes.getString(R.string.password_is_null));
            return false;
        }
        if (password.contains(" ")) {
            NToast.shortToast(mCtx, mRes.getString(R.string.password_cannot_contain_spaces));
            return false;
        }
        return true;
    }

    private boolean checkAllParams() {
        String phone = mPhoneNumberEt.getText().toString().trim();
        String code = mVerifyEt.getText().toString().trim();
        String password = mPassdEt.getText().toString().trim();

        if (TextUtils.isEmpty(phone)) {
            NToast.shortToast(mCtx, mRes.getString(R.string.phone_number_is_null));
            return false;
        }
        if (TextUtils.isEmpty(code)) {
            NToast.shortToast(mCtx, mRes.getString(R.string.code_is_null));
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            NToast.shortToast(mCtx, mRes.getString(R.string.password_is_null));
            return false;
        }
        if (password.contains(" ")) {
            NToast.shortToast(mCtx, mRes.getString(R.string.password_cannot_contain_spaces));
            return false;
        }
        return true;
    }

    public void setRegisterButtonOnclickListener(View.OnClickListener registerButtonOnclickListener) {
        this.mRegisterButtonOnclickListener = registerButtonOnclickListener;
    }

    public void resetRegisterButton() {
        mRegisterBtn.reset();
    }

    @Override
    public void onRequestVerifySuccess() {
        NToast.shortToast(mAppCtx,mRes.getString(R.string.send_code_success));
        mRegisterBtn.setEnabled(true);
    }

    @Override
    public void onRequestVerifyError(int code, String message) {
        NToast.longToast(mAppCtx,mRes.getString(R.string.send_code_failed) + ":" + message);
        mRegisterBtn.setEnabled(false);
    }
}

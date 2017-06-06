package com.zuowei.utils.handlers;

import com.zuowei.utils.bridge.constant.EaterAction;
import com.zuowei.utils.bridge.handler.ToolKit;
import com.zuowei.utils.bridge.params.LightParam;
import com.zuowei.utils.bridge.params.LoginParam;
import com.zuowei.utils.bridge.params.ParamWrap;
import com.zuowei.utils.helper.HiTalkHelper;
import com.zuowei.utils.helper.LoginHelper;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by zuowei on 16-7-18.
 */
public class LoginHandler extends AbstractHandler<LoginParam>{

    private static LoginHandler sInstance;
    public interface OnLoginListener{
        void onSuccess();
        void onError(int code,String message);
    }

    public interface  OnRegisterListener{
        void onRegisterSuccess();
        void onRegisterError(int code,String message);
    }

    public interface OnVerifyListener{
        void onVerifySuccess();
        void onVerifyError(int code,String message);
    }

    public interface OnRequestVerifyListener{
        void onRequestVerifySuccess();
        void onRequestVerifyError(int code,String message);
    }

    private CopyOnWriteArrayList<OnLoginListener> mOnLoginListeners;
    private CopyOnWriteArrayList<OnRegisterListener> mOnRegisterListeners;
    private CopyOnWriteArrayList<OnVerifyListener> onVerifyListeners_;
    private CopyOnWriteArrayList<OnRequestVerifyListener> onRequestVerifyListeners_;


    public static LoginHandler getInstance(){
        if (sInstance == null){
            synchronized (LoginHandler.class){
                if (sInstance == null){
                    sInstance = new LoginHandler();
                }
            }
        }
        return sInstance;
    }

    private LoginHandler(){
        mOnLoginListeners = new CopyOnWriteArrayList<>();
        mOnRegisterListeners = new CopyOnWriteArrayList<>();
        onVerifyListeners_ = new CopyOnWriteArrayList<>();
        onRequestVerifyListeners_ = new CopyOnWriteArrayList<>();
    }

    @Override
    public boolean isParamAvailable(LightParam param) {
        return param != null && EaterAction.ACTION_DO_LOGIN.equals(param.getAction());
    }

    @Override
    public void doJobWithParam(ParamWrap<LoginParam> paramWrap) {
        LightParam param = paramWrap.getParam();
        int type = param.getData().getInt("type");
        String userName = param.getData().getString("userName");
        String password = param.getData().getString("password");
        String phoneNumber = param.getData().getString("mobilePhoneNumber");
        String code = param.getData().getString("code");
        switch (type){
            case LoginParam.TYPE_LOGIN:
                doLogin(userName,password);
                break;
            case LoginParam.TYPE_REGISTER:
                doRegister(userName,password,phoneNumber);
                break;
            case LoginParam.TYPE_REQUEST_VERIFY:
                doRequestVerify(phoneNumber);
                break;
            case LoginParam.TYPE_VERIFY:
                doVerify(code,phoneNumber);
                break;
            case LoginParam.TYPE_LOGOUT:
                doLogout();
                break;
        }
    }

    private void doLogout() {
        LoginHelper.getInstance().logout();
    }

    private void doVerify(String code,String phoneNumber) {
        LoginHelper.getInstance().verifyMobilePhone(code,phoneNumber);
    }

    private void doRequestVerify(String phoneNumber) {
        LoginHelper.getInstance().requestVerifyMobilePhone(phoneNumber);
    }

    private void doRegister(String userName, String password, String phoneNumber) {
        LoginHelper.getInstance().register(userName,password,phoneNumber);
    }

    private void doLogin(String userName, String password) {
        HiTalkHelper.getInstance().getModel().setCurrentUserName(userName);
        //do login
        LoginHelper.getInstance().login(userName,password);
    }

    public void addLoginListener(OnLoginListener onLoginListener){
        mOnLoginListeners.add(onLoginListener);
    }

    public void removeLoginListener(OnLoginListener onLoginListener){
        mOnLoginListeners.remove(onLoginListener);
    }
    public void addRegisterListener(OnRegisterListener onRegisterListener){
        mOnRegisterListeners.add(onRegisterListener);
    }

    public void removeRegisterListener(OnRegisterListener onRegisterListener){
        mOnRegisterListeners.remove(onRegisterListener);
    }

    public void addRequestVerifyListener(OnRequestVerifyListener onRequestVerifyListener){
        onRequestVerifyListeners_.add(onRequestVerifyListener);
    }

    public void removeRequestVerifyListener(OnRequestVerifyListener onRequestVerifyListener){
        onRequestVerifyListeners_.remove(onRequestVerifyListener);
    }

    public void addVerifyListener(OnVerifyListener onVerifyListener){
        onVerifyListeners_.add(onVerifyListener);
    }

    public void removeVerifyListener(OnVerifyListener onVerifyListener){
        onVerifyListeners_.remove(onVerifyListener);
    }

    public void loginSuccess(){
        ToolKit.runOnMainThreadAsync(() -> {
            for (OnLoginListener loginListener : mOnLoginListeners){
                loginListener.onSuccess();
            }
        });
    }

    public void loginError(final int code, final String message){
        ToolKit.runOnMainThreadAsync(() -> {
            for (OnLoginListener loginListener : mOnLoginListeners){
                loginListener.onError(code,message);
            }
        });
    }

    public void registerSuccess(){
        ToolKit.runOnMainThreadAsync(() -> {
            for (OnRegisterListener registerListener : mOnRegisterListeners){
                registerListener.onRegisterSuccess();
            }
        });
    }

    public void registerError(final int code, final String message){
        ToolKit.runOnMainThreadAsync(() -> {
            for (OnRegisterListener registerListener : mOnRegisterListeners){
                registerListener.onRegisterError(code,message);
            }
        });
    }

    public void requestSuccess(){
        ToolKit.runOnMainThreadAsync(() -> {
            for (OnRequestVerifyListener onRequestVerifyListener : onRequestVerifyListeners_){
                onRequestVerifyListener.onRequestVerifySuccess();
            }
        });
    }

    public void requestError(final int code, final String message){
        ToolKit.runOnMainThreadAsync(() -> {
            for (OnRequestVerifyListener onRequestVerifyListener : onRequestVerifyListeners_){
                onRequestVerifyListener.onRequestVerifyError(code,message);
            }
        });
    }

    public void verifySuccess(){
        ToolKit.runOnMainThreadAsync(() -> {
            for (OnVerifyListener onVerifyListener : onVerifyListeners_){
                onVerifyListener.onVerifySuccess();
            }
        });
    }

    public void verifyError(final int code, final String message){
        ToolKit.runOnMainThreadAsync(() -> {
            for (OnVerifyListener onVerifyListener : onVerifyListeners_){
                onVerifyListener.onVerifyError(code,message);
            }
        });
    }
}

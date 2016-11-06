package com.zuowei.utils.helper;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVMobilePhoneVerifyCallback;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.RequestMobileCodeCallback;
import com.avos.avoscloud.SignUpCallback;
import com.vivifram.second.hitalk.bean.AvResult;
import com.vivifram.second.hitalk.bean.RequestVerifyBody;
import com.vivifram.second.hitalk.manager.RetrofitManager;
import com.zuowei.utils.common.NLog;
import com.zuowei.utils.common.RxjavaUtils;
import com.zuowei.utils.common.TagUtil;
import com.zuowei.utils.handlers.LoginHandler;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zuowei on 16-7-18.
 */
public class LoginHelper {

    private static LoginHelper sInstance;
    private LogInCallback mLoginCallback;
    private SignUpCallback mRegisterCallback;
    private RequestMobileCodeCallback mobileCodeCallback_;
    private AVMobilePhoneVerifyCallback mobilePhoneVerifyCallback_;

    private LoginHelper(){
        mLoginCallback = new LogInCallback() {
            @Override
            public void done(AVUser avUser, AVException e) {
                NLog.i(TagUtil.makeTag(LoginHelper.class),"done = "+e);
                if (e != null) {
                    LoginHandler.getInstance().loginError(e.getCode(), e.getMessage());
                }else {
                    LoginHandler.getInstance().loginSuccess();
                }
            }
        };

        mRegisterCallback = new SignUpCallback() {
            @Override
            public void done(AVException e) {
                if (e != null) {
                    LoginHandler.getInstance().registerError(e.getCode(), e.getMessage());
                }else {
                    LoginHandler.getInstance().registerSuccess();
                }
            }
        };

        mobileCodeCallback_ = new RequestMobileCodeCallback() {
            @Override
            public void done(AVException e) {
                if (e != null) {
                    LoginHandler.getInstance().requestError(e.getCode(), e.getMessage());
                }else {
                    LoginHandler.getInstance().requestSuccess();
                }
            }
        };

        mobilePhoneVerifyCallback_ = new AVMobilePhoneVerifyCallback() {
            @Override
            public void done(AVException e) {
                if (e != null) {
                    LoginHandler.getInstance().verifyError(e.getCode(), e.getMessage());
                }else {
                    LoginHandler.getInstance().verifySuccess();
                }
            }
        };
    }


    public static LoginHelper getInstance(){
        if (sInstance == null){
            synchronized (LoginHelper.class){
                if (sInstance == null){
                    sInstance = new LoginHelper();
                }
            }
        }
        return sInstance;
    }

    public void login(String userName, String password){
        AVUser.logInInBackground(userName,password,mLoginCallback);
    }

    public void register(String userName,String password,String phoneNumber){
        AVUser avUser = new AVUser();
        avUser.setUsername(userName);
        avUser.setPassword(password);
        avUser.put("mobilePhoneNumber",phoneNumber);
        avUser.signUpInBackground(mRegisterCallback);
    }

    public void verifyMobilePhone(String code,String phoneNumber){
        mobilePhoneVerifyCallback_.done(null);
        /*ILoginService loginService =
                RetrofitManager.getInstance().create(ILoginService.class,"https://api.leancloud.cn");

        RxjavaUtils.ObServerOnMainThread(loginService.verifySms(code,phoneNumber),
                new Subscriber<AvResult>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        NLog.e(TagUtil.makeTag(LoginHelper.class),"e = "+e);
                        mobilePhoneVerifyCallback_.done(new AvResult(AVException.UNKNOWN,"Unknown"));
                    }

                    @Override
                    public void onNext(AvResult avResult) {
                        NLog.e(TagUtil.makeTag(LoginHelper.class),"avResult = "+avResult);
                        if (avResult.getCode() == 0 &&
                                avResult.getMessage() == null){
                            mobilePhoneVerifyCallback_.done(null);
                        }else {
                            mobilePhoneVerifyCallback_.done(avResult);
                        }
                    }
                });*/
    }

    public void requestVerifyMobilePhone(String phoneNumber){
        ILoginService loginService =
                RetrofitManager.getInstance().create(ILoginService.class,"https://api.leancloud.cn");
        RxjavaUtils.ObServerOnMainThread(loginService.requestSms(
                RequestVerifyBody.build(phoneNumber,10)),
                new Subscriber<AvResult>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        NLog.e(TagUtil.makeTag(LoginHelper.class),"e = "+e);
                        mobileCodeCallback_.done(new AvResult(AVException.UNKNOWN,"Unknown"));
                    }

                    @Override
                    public void onNext(AvResult avResult) {
                        NLog.e(TagUtil.makeTag(LoginHelper.class),"avResult = "+avResult);
                        if (avResult.getCode() == 0 &&
                                avResult.getMessage() == null){
                            mobileCodeCallback_.done(null);
                        }else {
                            mobileCodeCallback_.done(avResult);
                        }
                    }
                });
    }

    public void logout(){
        AVUser.logOut();
    }

}

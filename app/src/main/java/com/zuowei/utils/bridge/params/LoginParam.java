package com.zuowei.utils.bridge.params;

import android.os.Bundle;

import com.zuowei.utils.bridge.constant.EaterAction;
import com.zuowei.utils.common.NLog;
import com.zuowei.utils.common.TagUtil;

/**
 * Created by zuowei on 16-7-18.
 */
public class LoginParam extends LightParam{

    public static final int TYPE_LOGIN = 0x01;
    public static final int TYPE_REGISTER = 0x02;
    public static final int TYPE_REQUEST_VERIFY = 0x03;
    public static final int TYPE_VERIFY = 0x04;
    public static final int TYPE_LOGOUT = 0X05;

    private LoginParam(Builder builder) {
        super(EaterAction.ACTION_DO_LOGIN);
        setData(builder.mParam);
    }

    public static class Builder{
        private Bundle mParam;
        public Builder(int type){
            mParam = new Bundle();
            mParam.putInt("type",type);
        }

        public Builder setUserName(String userName){
            mParam.putString("userName",userName);
            return this;
        }

        public Builder setPassword(String password){
            mParam.putString("password",password);
            return this;
        }

        public Builder setPhoneNumber(String phoneNumber){
            mParam.putString("mobilePhoneNumber",phoneNumber);
            return this;
        }

        public Builder setCode(String code){
            mParam.putString("code",code);
            return this;
        }

        public LoginParam create(){
            return new LoginParam(this);
        }
    }
}

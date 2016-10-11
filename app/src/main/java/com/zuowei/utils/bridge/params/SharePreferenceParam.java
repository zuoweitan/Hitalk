package com.zuowei.utils.bridge.params;

import android.os.Bundle;

import com.zuowei.utils.bridge.constant.EaterAction;

/**
 * Created by zuowei on 16-4-6.
 */
public class SharePreferenceParam extends LightParam {

    private Bundle mData;
    public SharePreferenceParam() {
        super(EaterAction.ACTION_DO_WITH_PREFERENCE);
        mData =  new Bundle();
        setData(mData);
    }

    public SharePreferenceParam(String name,Object value){
        this();
        setName(name);
        setValue(value);
    }

    public void setName(String name){
        mData.putString("name",name);
    }

    public void setValue(Object value){
        if (value instanceof Boolean){
            mData.putBoolean("value", (Boolean) value);
        }
        if (value instanceof Float){
            mData.putFloat("value", (Float) value);
        }
        if (value instanceof Integer){
            mData.putInt("value", (Integer) value);
        }
        if (value instanceof Long){
            mData.putLong("value", (Long) value);
        }
        if (value instanceof String){
            mData.putString("value", (String) value);
        }
    }
}

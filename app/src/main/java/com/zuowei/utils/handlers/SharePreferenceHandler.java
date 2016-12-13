package com.zuowei.utils.handlers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.zuowei.utils.bridge.constant.EaterAction;
import com.zuowei.utils.bridge.params.LightParam;
import com.zuowei.utils.bridge.params.SharePreferenceParam;
import com.zuowei.utils.common.TagUtil;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * Created by zuowei on 16-4-6.
 */
public class SharePreferenceHandler extends AbstractHandler<SharePreferenceParam> {
    private static final String TAG = TagUtil.makeTag(SharePreferenceHandler.class);

    private static SharePreferenceHandler sInstance;
    private static final String NAME_SHAREPREFERENCE = "light_data";
    private static boolean mNewInstanceCalled;
    private Context mContext;

    public static SharePreferenceHandler getInstance(){
        if (sInstance == null){
            synchronized (SharePreferenceHandler.class){
                if (sInstance == null){
                    mNewInstanceCalled = true;
                    sInstance = new SharePreferenceHandler();
                }
            }
        }
        return sInstance;
    }

    private SharePreferenceHandler(){}

    public SharePreferenceHandler setContext (Context context){
        mContext = context;
        return this;
    }

    @Override
    public boolean isParamAvailable(LightParam param) {
        if (param != null && EaterAction.ACTION_DO_WITH_PREFERENCE.equals(param.getAction())){
            if (param instanceof SharePreferenceParam){
                return true;
            }
        }
        return false;
    }

    @Override
    public void doJobWithParam(SharePreferenceParam param) {
        SharePreferenceParam temp = param;
        Bundle data = temp.getData();
        if (data != null){
            final String name = data.getString("name");
            final Object value = data.get("value");
            Observable.just(NAME_SHAREPREFERENCE)
                    .map(new Func1<String, String>() {
                        @Override
                        public String call(String s) {
                            SharedPreferences sharedPreferences = mContext.getSharedPreferences(s,Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            switch (checkType(value)){
                                case 1:
                                    editor.putBoolean(name, (Boolean) value);
                                    break;
                                case 2:
                                    editor.putFloat(name, (Float) value);
                                    break;
                                case 3:
                                    editor.putInt(name, (Integer) value);
                                    break;
                                case 4:
                                    editor.putLong(name, (Long) value);
                                    break;
                                case 5:
                                    editor.putString(name, (String) value);
                                    break;
                            }
                            editor.apply();
                            return "done";
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Action1<String>() {
                        @Override
                        public void call(String s) {
                        }
                    });
        }
    }

    private int checkType(Object value){
        if (value instanceof Boolean){
            return 1;
        }
        if (value instanceof Float){
            return 2;
        }
        if (value instanceof Integer){
            return 3;
        }
        if (value instanceof Long){
            return 4;
        }
        if (value instanceof String){
            return 5;
        }
        return -1;
    }

    public boolean getBoolean(String name,boolean defaultValue){
        return mContext.getSharedPreferences(NAME_SHAREPREFERENCE,Context.MODE_PRIVATE).getBoolean(name,defaultValue);
    }

    public float getFloat(String name,float defaultValue){
        return mContext.getSharedPreferences(NAME_SHAREPREFERENCE,Context.MODE_PRIVATE).getFloat(name,defaultValue);
    }

    public int getInt(String name,int defaultValue){
        return mContext.getSharedPreferences(NAME_SHAREPREFERENCE,Context.MODE_PRIVATE).getInt(name,defaultValue);
    }

    public long getLong(String name,long defaultValue){
        return mContext.getSharedPreferences(NAME_SHAREPREFERENCE,Context.MODE_PRIVATE).getLong(name,defaultValue);
    }

    public String getString(String name,String defaultValue){
        return mContext.getSharedPreferences(NAME_SHAREPREFERENCE,Context.MODE_PRIVATE).getString(name,defaultValue);
    }

    private SharedPreferences getLocalPreferences(){
        return mContext.getSharedPreferences(NAME_SHAREPREFERENCE,Context.MODE_PRIVATE);
    }
}

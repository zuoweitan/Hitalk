package com.vivifram.second.hitalk.ui.layout;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;

import com.vivifram.second.hitalk.HiTalkApplication;

/**
 * Created by zuowei on 16-7-18.
 */
public class BaseLayout {
    protected View mRootView;
    protected Context mCtx;
    protected Context mAppCtx;
    protected Resources mRes;
    protected LayoutInflater mLayoutInflater;
    protected OnLayoutEventListener mOnLayoutEventListener;
    public interface OnLayoutEventListener <T extends BaseLayout>{
        void onContentConfirmed(T t);
    }

    public BaseLayout (View rootView){
        mRootView = rootView;
        mCtx = mRootView.getContext();
        mRes = mRootView.getResources();
        mAppCtx = HiTalkApplication.mAppContext;
        mLayoutInflater = LayoutInflater.from(mCtx);
    }

    public View findViewById(int id){
        return mRootView.findViewById(id);
    }

    public void onContentViewCreate(View view){}

    public void onWindowAttached(){}

    public void onWindowDetached(){}

    public void onResume(){}

    public void onStop(){}

    public void onDestroy(){}

    public void setOnLayoutEventListener (OnLayoutEventListener onLayoutEventListener){
        mOnLayoutEventListener = onLayoutEventListener;
    }
}

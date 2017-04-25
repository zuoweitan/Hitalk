package com.vivifram.second.hitalk.ui.page.layout;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;

import com.vivifram.second.hitalk.HiTalkApplication;
import com.vivifram.second.hitalk.ui.layout.BaseLayout;
import com.zuowei.utils.bridge.params.ParamWrap;
import com.zuowei.utils.bridge.params.chat.ClientOpenParam;
import com.zuowei.utils.handlers.ClientOpenHandler;

/**
 * Created by zuowei on 16-8-1.
 */
public class BaseFragmentLayout implements View.OnClickListener{

    protected View mRootView;
    protected Context mCtx;
    protected Activity mAct;
    protected Context mAppCtx;
    protected Resources mRes;
    protected LayoutInflater mLayoutInflater;
    protected BaseLayout.OnLayoutEventListener mOnLayoutEventListener;

    public BaseFragmentLayout(View root){
        mRootView = root;
        mCtx = mRootView.getContext();
        mRes = mRootView.getResources();
        mAppCtx = HiTalkApplication.mAppContext;
        mLayoutInflater = LayoutInflater.from(mCtx);
    }

    public void setActivity(Activity act){
        mAct = act;
    }

    public View findViewById(int id){
        return mRootView.findViewById(id);
    }

    public void onViewCreate(View root){
    }

    public void onActivitySet(){}

    public void onStart(){}

    public void onResume(){}

    public void onPause(){}

    public void onStop(){}

    public void onViewDestroy(){
    }

    public void onDestroy(){}

    public void onClientOpen(){}

    private ClientOpenHandler clientOpenHandler = new ClientOpenHandler() {
        @Override
        public void doJobWithParam(ParamWrap<ClientOpenParam> paramWrap) {
            if (paramWrap.getParam().mOpened){
                //onClientOpen();
            }
        }
    };

    @Override
    public void onClick(View v) {

    }
}

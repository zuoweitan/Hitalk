package com.vivifram.second.hitalk.ui.layout;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;

import com.vivifram.second.hitalk.HiTalkApplication;
import com.zuowei.utils.bridge.EaterManager;
import com.zuowei.utils.bridge.constant.EaterAction;
import com.zuowei.utils.bridge.params.ParamWrap;
import com.zuowei.utils.bridge.params.chat.ClientOpenParam;
import com.zuowei.utils.handlers.ClientOpenHandler;

/**
 * Created by zuowei on 16-7-18.
 */
public class BaseLayout implements View.OnClickListener{
    protected View mRootView;
    protected Context mCtx;
    protected Context mAppCtx;
    protected Resources mRes;
    protected LayoutInflater mLayoutInflater;
    protected OnLayoutEventListener mOnLayoutEventListener;

    @Override
    public void onClick(View v) {

    }

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

    public void onContentViewCreate(View view){
    }

    public void onWindowAttached(){}

    public void onWindowDetached(){}

    public void onResume(){}

    public void onStop(){}

    public void onDestroy(){
    }

    public void onClientOpen(){}

    public void setOnLayoutEventListener (OnLayoutEventListener onLayoutEventListener){
        mOnLayoutEventListener = onLayoutEventListener;
    }

    private ClientOpenHandler clientOpenHandler = new ClientOpenHandler() {
        @Override
        public void doJobWithParam(ParamWrap<ClientOpenParam> paramWrap) {
            if (paramWrap.getParam().mOpened){
                //onClientOpen();
            }
        }
    };

}

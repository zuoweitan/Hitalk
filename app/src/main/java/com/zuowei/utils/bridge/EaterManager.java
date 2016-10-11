package com.zuowei.utils.bridge;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import com.zuowei.utils.bridge.params.LightParam;
import com.zuowei.utils.common.TagUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by zuowei on 16-4-6.
 */
public class EaterManager {
    private static final String TAG = TagUtil.makeTag(EaterManager.class);
    private final static ExecutorService DEFAULT_EXECUTOR_SERVICE = Executors.newCachedThreadPool();
    private static final int MSG_REGISTER = 0x01;
    private static final int MSG_UNREGISTER = 0x02;
    private static final int MSG_DO_BROADCAST = 0x03;
    private static EaterManager sInstance;
    private Handler mHandler;
    private HashMap<String,List<IEater>> mEaterEntries;
    private Map<String,LightParam> mStickyActions;

    public static EaterManager getInstance(){
        if (sInstance == null){
            synchronized (EaterManager.class){
                if (sInstance == null){
                    sInstance = new EaterManager();
                }
            }
        }
        return sInstance;
    }

    private EaterManager(){
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case MSG_REGISTER:
                            IEater eater = (IEater) msg.obj;
                            Bundle ac = msg.getData();
                            if (ac != null){
                                String action = ac.getString("action");
                                registerEaterInner(action,eater);
                            }
                        break;
                    case MSG_UNREGISTER:
                            eater = (IEater) msg.obj;
                            unRegisterEaterInner(eater);
                        break;

                    case MSG_DO_BROADCAST:
                            LightParam lightParam = (LightParam) msg.obj;
                            doBroadCast(lightParam);
                        break;
                }
            }
        };
        mEaterEntries = new HashMap<>();
        mStickyActions = Collections.synchronizedMap(new HashMap<String,LightParam>());
    }

    public void registerEater(String action,IEater eater){
        if (action == null){
            throw new RuntimeException("action can not be null");
        }
        Message msg = new Message();
        msg.what = MSG_REGISTER;
        msg.obj = eater;
        Bundle ac = new Bundle();
        ac.putString("action",action);
        msg.setData(ac);
        mHandler.sendMessage(msg);
    }

    private void registerEaterInner(String action, IEater eater) {
        boolean isAdd = false;
        if (mEaterEntries.containsKey(action)){
            List<IEater> eaters = mEaterEntries.get(action);
            if (!eaters.contains(eater)){
                eaters.add(eater);
                isAdd = true;
            }
        }else {
            List<IEater> eaters = new CopyOnWriteArrayList<>();
            eaters.add(eater);
            mEaterEntries.put(action,eaters);
            isAdd = true;
        }
        if (isAdd){
            if (mStickyActions.containsKey(action)){
                eater.onEat(mStickyActions.get(action));
            }
        }
    }

    public void unRegisterEater(IEater eater){
        if (eater == null){
            throw new RuntimeException("eater can not be null");
        }
        Message msg = new Message();
        msg.what = MSG_UNREGISTER;
        msg.obj = eater;
        mHandler.sendMessage(msg);
    }

    private void unRegisterEaterInner(IEater eater){
        String target = null;
        boolean doRemove = false;
        for (String key : mEaterEntries.keySet()){
            List<IEater> value = mEaterEntries.get(key);
            if (value != null) {
                for (int i = 0; i < value.size();i++){
                    IEater temp = value.get(i);
                    if (eater.equals(temp)){
                        value.remove(i);
                        doRemove = true;
                        break;
                    }
                }
            }
            if (doRemove) {
                if (value.size() == 0) {
                    target = key;
                }
                break;
            }
        }

        if (target != null){
            mEaterEntries.remove(target);
        }
    }

    public void broadcast(LightParam param){
        if (param == null || param.getAction() == null){
            throw new RuntimeException("param and it's action can not be null");
        }
        mStickyActions.remove(param.getAction());
        Message msg = new Message();
        msg.what = MSG_DO_BROADCAST;
        msg.obj = param;
        mHandler.sendMessage(msg);
    }

    public void broadCastSticky(LightParam param){
        if (param == null || param.getAction() == null){
            throw new RuntimeException("param and it's action can not be null");
        }
        mStickyActions.put(param.getAction(),param);
        Message msg = new Message();
        msg.what = MSG_DO_BROADCAST;
        msg.obj = param;
        mHandler.sendMessage(msg);
    }



    /*public void broadcastBackground(final LightParam param){
        if (param == null || param.getAction() == null){
            throw new RuntimeException("param and it's action can not be null");
        }
        Observable.just("broadcastBackground")
                .subscribeOn(Schedulers.from(DEFAULT_EXECUTOR_SERVICE))
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        doBroadCast(param);
                    }
                });
    }*/

    private void doBroadCast(LightParam param){
        List<IEater> eaters = mEaterEntries.get(param.getAction());
        if (eaters != null){
            for (IEater eater : eaters){
                eater.onEat(param);
            }
        }
    }
}

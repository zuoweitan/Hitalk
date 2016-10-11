package com.zuowei.utils.helper;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vivifram.second.hitalk.bean.blackboard.BnItem;
import com.zuowei.utils.bridge.EaterManager;
import com.zuowei.utils.bridge.params.SharePreferenceParam;
import com.zuowei.utils.common.NLog;
import com.zuowei.utils.common.TagUtil;
import com.zuowei.utils.handlers.SharePreferenceHandler;

import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * Created by zuowei on 16-9-6.
 */
public class StaticDataCacheHelper {
    private static StaticDataCacheHelper sInstance;

    private HashMap<String,BnItem> bnItemDoor;
    private HashMap<String,Integer> bnValidEntries;
    private Handler mHandler;
    public static StaticDataCacheHelper getInstance() {
        if (sInstance == null) {
            synchronized (StaticDataCacheHelper.class){
                if (sInstance == null) {
                    sInstance = new StaticDataCacheHelper();
                }
            }
        }
        return sInstance;
    }

    private StaticDataCacheHelper(){
        bnItemDoor = new HashMap<>();
        mHandler = new Handler(Looper.getMainLooper());
        String validEntries = SharePreferenceHandler.getInstance().getString("VALID_BN_ITEMS",null);
        if (validEntries == null){
            bnValidEntries = new HashMap<>();
        }else {
            try {
                Type type = new TypeToken<HashMap<String, Integer>>(){}.getType();
                bnValidEntries = new Gson().fromJson(validEntries,type);
            }catch (Exception e){
                NLog.i(TagUtil.makeTag(getClass()),"bnValidEntries init ",e);
                bnValidEntries = new HashMap<>();
            }
        }
    }

    public synchronized void cacheBnItem(String key,BnItem bnItem){
        bnItemDoor.clear();
        bnItemDoor.put(key,bnItem);
    }

    public BnItem getBnItem(String key){
        return bnItemDoor.get(key);
    }

    public boolean checkValid(String bnId){

        if (bnValidEntries == null || bnValidEntries.get(bnId) == null){
            return true;
        }
        int type = bnValidEntries.get(bnId);
        return type == 0;
    }

    public synchronized void setItemValid(String bnId,int type){
        if (type != 0){
            type = 1;
        }
        if (bnValidEntries == null){
            bnValidEntries = new HashMap<>();
        }
        if (type == 0){
            bnValidEntries.remove(bnId);
        }else {
            bnValidEntries.put(bnId,type);
        }

        EaterManager.getInstance().broadcast(new SharePreferenceParam("VALID_BN_ITEMS",new Gson().toJson(bnValidEntries)));
    }
}

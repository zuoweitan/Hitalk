package com.vivifram.second.hitalk;

import android.app.Application;
import android.content.Context;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.vivifram.second.hitalk.bean.blackboard.BnRemote;
import com.vivifram.second.hitalk.broadcast.ConnectivityNotifier;
import com.zuowei.utils.common.ApplicationUtils;
import com.zuowei.utils.common.NLog;
import com.zuowei.utils.common.TagUtil;
import com.zuowei.utils.helper.DaoHelper;
import com.zuowei.utils.helper.HiTalkHelper;
import com.zuowei.utils.helper.NineGridHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zuowei on 16-7-13.
 */
public class HiTalkApplication extends Application {

    public static Context mAppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        if (getPackageName().equals(ApplicationUtils.getCurrentProcessName(this))){
            mAppContext = this;
            DaoHelper.getInstance().init(mAppContext);
            NineGridHelper.initWithGlide();
            ConnectivityNotifier.getNotifier(mAppContext);
            HiTalkHelper.getInstance().init(mAppContext);
            //start Queue
            //test
            //tryGetAllBnRemotes();

            //tryHashMapAndList();

        }
    }

    private void tryHashMapAndList() {
        boolean bool = true;
        int number = 2015;
        String string = number + " 年度音乐排行";
        Date date = new Date();

        byte[] data = "短篇小说".getBytes();
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(number);
        arrayList.add(string);
        HashMap<Object, Object> hashMap = new HashMap<>();
        hashMap.put("数字", number);
        hashMap.put("字符串", string);

        AVObject object = new AVObject("DataTypes");
        object.put("testBoolean", bool);
        object.put("testInteger", number);
        object.put("testDate", date);
        object.put("testData", data);
        object.put("testArrayList", arrayList);
        object.put("testHashMap", hashMap);
        object.saveInBackground();
    }

    private void tryGetAllBnRemotes() {
        NLog.i(TagUtil.makeTag(getClass()),"tryGetAllBnRemotes");
        AVQuery<BnRemote> query = AVQuery.getQuery(BnRemote.class);
        query.whereEqualTo("isAll",true);
        query.findInBackground(new FindCallback<BnRemote>() {
            @Override
            public void done(List<BnRemote> list, AVException e) {
                NLog.i(TagUtil.makeTag(getClass()),"bnRemote list = "+list);
            }
        });
    }

    public static Context $() {
        return mAppContext;
    }
}

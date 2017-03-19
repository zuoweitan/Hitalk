package com.zuowei.utils.helper;

import com.zuowei.dao.greendao.Schoolmate;
import com.zuowei.dao.greendao.SchoolmateDao;
import com.zuowei.utils.common.NLog;
import com.zuowei.utils.common.TagUtil;

import java.util.Date;
import java.util.HashMap;

import bolts.Task;
import de.greenrobot.dao.async.AsyncSession;
import de.greenrobot.dao.query.Query;

/**
 * Created by zuowei on 16-11-6.
 */

public class SchoolmatesCacheHelper {

    public static final int REQUEST_STATE_SUCCESS = 0x01;
    public static final int REQUEST_STATE_WATING = 0x02;
    public static final int REQUEST_STATE_FAILED = 0x03;

    private static final SchoolmatesCacheHelper schoolmatesCacheHelper
            = new SchoolmatesCacheHelper();

    public static SchoolmatesCacheHelper getInstance(){
        return schoolmatesCacheHelper;
    }

    private SchoolmatesCacheHelper(){
        caches = new HashMap<>();
    }

    private HashMap<String,Integer> caches;


    public Task<Integer> getSchoolmateFriendState(String uid){
        if (caches.get(uid) == null){
            return Task.callInBackground(() -> {
                Query<Schoolmate> build = DaoHelper.getInstance().getSchoolmateDao().queryBuilder()
                        .where(SchoolmateDao.Properties.UserId.eq(uid))
                        .build();
                int friendState = REQUEST_STATE_FAILED;
                try{
                    friendState = build.unique().getFriendState();
                }catch (Exception e){
                    NLog.e(TagUtil.makeTag(getClass()),"getSchoolmateFriendState failed ",e);
                }
                caches.put(uid,friendState);
                return friendState;
            });
        }else {
            return Task.forResult(caches.get(uid));
        }
    }

    public void cache(String uid,int friendState){
        if (!caches.containsKey(uid)) {
            caches.put(uid, friendState);
            AsyncSession asyncSession = DaoHelper.getInstance().wrapDao(DaoHelper.getInstance().getSchoolmateDao());
            asyncSession.insertOrReplaceInTx(Schoolmate.class, new Schoolmate(uid, friendState, new Date()));
        }
    }

    public void cache(String uid,int friendState, boolean force){
        if (force) {
            caches.remove(uid);
            cache(uid, friendState);
        } else {
            cache(uid, friendState);
        }
    }

    public void update(String uid,int state){
        cache(uid,state,true);
    }

    @Override
    public String toString() {
        return "SchoolmatesCacheHelper{" +
                "caches=" + caches +
                '}';
    }
}

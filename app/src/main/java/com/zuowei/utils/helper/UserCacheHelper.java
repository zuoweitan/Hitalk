package com.zuowei.utils.helper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.vivifram.second.hitalk.bean.UserWrap;
import com.zuowei.dao.greendao.User;
import com.zuowei.utils.common.NLog;
import com.zuowei.utils.common.PathUtils;
import com.zuowei.utils.common.TagUtil;
import com.zuowei.utils.common.Utils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by zuowei on 16-8-2.
 */
public class UserCacheHelper {
    private static final String TAG = TagUtil.makeTag(UserCacheHelper.class);
    public static final String AVATAR = "avatar";
    public static final String LOCATION = "location";
    public static final String OBJECT_ID = "objectId";
    public static final String INSTALLATION = "installation";
    private static Map<String, AVUser> mAvUsersCache;

    private static UserCacheHelper sInstance;
    private UserCacheHelper (){
        mAvUsersCache = new HashMap<>();
    }

    public static UserCacheHelper getInstance(){
        if (sInstance == null){
            synchronized (UserCacheHelper.class){
                if (sInstance == null){
                    sInstance = new UserCacheHelper();
                }
            }
        }
        return sInstance;
    }

    public AVUser getCachedAVUser(String objectId) {
        return mAvUsersCache.get(objectId);
    }

    public boolean hasCachedUser(String objectId) {
        return mAvUsersCache.containsKey(objectId);
    }

    public void cacheUser(AVUser user) {
        if (null != user && !TextUtils.isEmpty(user.getObjectId())) {
            mAvUsersCache.put(user.getObjectId(), user);
        }
    }

    public void cacheUsers(List<AVUser> users) {
        if (null != users) {
            for (AVUser user : users) {
                cacheUser(user);
            }
        }
    }

    public void fetchUsers(List<String> ids) {
        fetchUsers(ids, null);
    }

    public void fetchUsers(final List<String> ids, final CacheUserCallback cacheUserCallback) {

        Set<String> uncachedIds = new HashSet<String>();
        for (String id : ids) {
            if (!mAvUsersCache.containsKey(id)) {
                uncachedIds.add(id);
            }
        }
        if (uncachedIds.isEmpty()) {
            if (null != cacheUserCallback) {
                cacheUserCallback.done(getUsersFromCache(ids), null);
                return;
            }
        }
        AVQuery<AVUser> q = AVUser.getQuery();
        q.whereContainedIn(OBJECT_ID, uncachedIds);
        q.setLimit(1000);
        q.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        q.findInBackground(new FindCallback<AVUser>() {
            @Override
            public void done(List<AVUser> list, AVException e) {
                if (null == e) {
                    for (AVUser user : list) {
                        mAvUsersCache.put(user.getObjectId(), user);
                    }
                }
                if (null != cacheUserCallback) {
                    cacheUserCallback.done(getUsersFromCache(ids), e);
                }
            }
        });
    }

    public List<AVUser> getUsersFromCache(List<String> ids) {
        List<AVUser> userList = new ArrayList<AVUser>();
        for (String id : ids) {
            if (mAvUsersCache.containsKey(id)) {
                userList.add(mAvUsersCache.get(id));
            }
        }
        return userList;
    }

    public static AVUser fillAvUser(User user,AVUser avUser){
        if (user == null || avUser == null)
            return null;
        Field[] fields = user.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            try {
                if ("objectId".equals(field.getName())){
                     avUser.setObjectId((String) field.get(user));
                }else {
                    avUser.put(field.getName(), field.get(user));
                }
            } catch (IllegalAccessException e) {
                NLog.e(TagUtil.makeTag(UserCacheHelper.class),e);
            }
        }
        return avUser;
    }

    public void saveCropAvatarInBackgroud(final Intent data, final StringBuilder out) {
        Observable<Intent> observable = Observable.just(data);
        observable.map(new Func1<Intent, String>() {
            @Override
            public String call(Intent intent) {
                Bundle extras = data.getExtras();
                String path = null;
                if (extras != null) {
                    Bitmap bitmap = extras.getParcelable("data");
                    if (bitmap != null) {
                        path = PathUtils.getAvatarCropPath();
                        Utils.saveBitmap(path, bitmap);
                        if (bitmap != null && bitmap.isRecycled() == false) {
                            bitmap.recycle();
                        }
                    }
                }
                return path;
            }
        }).subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        out.append(s);
                    }
                });
    }


    public void saveUserToCloud(UserWrap datas){
        if (datas != null) {
            AVUser avUser = AVUser.getCurrentUser();
            if (avUser != null) {
                for (String key : datas.keySet()) {
                    avUser.put(key, datas.get(key));
                }
                avUser.saveInBackground();
            }
        }
    }

    public void saveAvatar(String path, final SaveCallback saveCallback) {
        final AVFile file;
        try {
            file = AVFile.withAbsoluteLocalPath(HiTalkHelper.getInstance().getCurrentUser().getUsername(), path);
            HiTalkHelper.getInstance().getCurrentUser().put(AVATAR, file);
            file.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    if (null == e) {
                        HiTalkHelper.getInstance().getCurrentUser().saveInBackground(saveCallback);
                    } else {
                        if (null != saveCallback) {
                            saveCallback.done(e);
                        }
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public AVGeoPoint getGeoPoint() {
        return HiTalkHelper.getInstance().getCurrentUser().getAVGeoPoint(LOCATION);
    }

    public void setGeoPoint(AVGeoPoint point) {
        HiTalkHelper.getInstance().getCurrentUser().put(LOCATION, point);
    }

    public String getAvatarUrl() {
        return getAvatarUrl(HiTalkHelper.getInstance().getCurrentUser());
    }

    public String getAvatarUrl(AVUser avUser){
        AVFile avatar = avUser.getAVFile(AVATAR);
        if (avatar != null) {
            return avatar.getUrl();
        } else {
            return null;
        }
    }

    public interface CacheUserCallback {
        void done(List<AVUser> userList, Exception e);
    }
}

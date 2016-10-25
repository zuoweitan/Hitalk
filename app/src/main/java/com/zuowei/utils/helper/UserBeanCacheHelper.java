package com.zuowei.utils.helper;

import com.avos.avoscloud.AVCallback;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.vivifram.second.hitalk.bean.UserWrap;
import com.vivifram.second.hitalk.state.DoneCallback;
import com.zuowei.dao.greendao.User;
import com.zuowei.dao.greendao.UserDao;
import com.zuowei.utils.provider.UserProvider;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by zuowei on 16-8-7.
 */
public class UserBeanCacheHelper {
    private static UserBeanCacheHelper sInstance;

    private Map<String, SoftReference<User>> mUsersBeanCache;
    private UserProvider mUserProvider;
    private UserBeanCacheHelper(){
        mUsersBeanCache = new HashMap();
    }

    public static UserBeanCacheHelper getInstance(){
        if (sInstance == null) {
            synchronized (UserBeanCacheHelper.class){
                if (sInstance == null) {
                    sInstance = new UserBeanCacheHelper();
                }
            }
        }
        return sInstance;
    }

    public void setUserProvider(UserProvider mUserProvider) {
        this.mUserProvider = mUserProvider;
    }

    public synchronized boolean hasCachedUser(String id) {
        return mUsersBeanCache.containsKey(id) && mUsersBeanCache.get(id).get() != null;
    }

    public synchronized void cacheUser(User user) {
        if(null != user) {
            mUsersBeanCache.put(user.getObjectId(),new SoftReference<User>(user));
            DaoHelper.getInstance().getAsyncSession().insertOrReplaceInTx(User.class,user);
        }

    }



    public synchronized void getCachedUser(String id, final AVCallback<User> callback) {
        getCachedUsers(Arrays.asList(new String[]{id}), new AVCallback<List<User>>() {
            @Override
            protected void internalDone0(List<User> users, AVException e) {
                User user = null != users && !users.isEmpty()?users.get(0):null;
                callback.internalDone(user, e);
            }
        });
    }


    public synchronized void getCachedUsers(List<String> idList, final AVCallback<List<User>> callback) {
        if(null != callback) {
            if(null != idList && !idList.isEmpty()) {
                final ArrayList<User> userList = new ArrayList();
                final ArrayList<String> unCachedIdList = new ArrayList();
                Iterator iterator = idList.iterator();

                while(iterator.hasNext()) {
                    String id = (String)iterator.next();
                    if(mUsersBeanCache.containsKey(id) && mUsersBeanCache.get(id).get() != null) {
                        userList.add(mUsersBeanCache.get(id).get());
                    } else {
                        unCachedIdList.add(id);
                    }
                }

                if(unCachedIdList.isEmpty()) {
                    callback.internalDone(userList, null);
                } else if(DaoHelper.getInstance().getUserDao() != null) {
                    UserDao userDao = DaoHelper.getInstance().getUserDao();
                    QueryBuilder<User> userQueryBuilder = userDao.queryBuilder();
                    userQueryBuilder.where(UserDao.Properties.ObjectId.in(unCachedIdList));
                    DaoHelper.getInstance().queryAsync(userQueryBuilder.build(), new AVCallback<List<User>>() {
                        @Override
                        protected void internalDone0(List<User> users, AVException e) {
                            if (users != null && !users.isEmpty() & users.size() == unCachedIdList.size()){
                                for (User user : users) {
                                    mUsersBeanCache.put(user.getObjectId(),new SoftReference<User>(user));
                                    userList.add(user);
                                }
                                callback.internalDone(userList, null);
                            }else {
                                getUsersFromProvider(unCachedIdList, userList, callback);
                            }
                        }
                    });
                } else {
                    getUsersFromProvider(unCachedIdList, userList, callback);
                }
            } else {
                callback.internalDone(null, new AVException(new Throwable("idList is empty!")));
            }
        }

    }

    private void getUsersFromProvider(List<String> idList, final List<User> users, final AVCallback<List<User>> callback) {
        if(null != mUserProvider) {
            mUserProvider.fetch(idList, (userList, e) -> {
                if(null != userList) {
                    userList.forEach((User user) -> {
                        cacheUser(user);
                    });
                    users.addAll(userList);
                }
                callback.internalDone(users, null != e?new AVException(e):null);
            });
        } else {
            callback.internalDone(null, new AVException(new Throwable("please setUserProvider first!")));
        }

    }

    public static void AvUserToUser(AVUser avUser, User user){
        user.setUserName(avUser.getUsername());
        user.setObjectId(avUser.getObjectId());
        user.setAvatar(UserCacheHelper.getInstance().getAvatarUrl(avUser));
        UserWrap userWrap = UserWrap.build(avUser);
        user.setNick(userWrap.getNickName());
        user.setSex(userWrap.getSex());
        user.setCollege(userWrap.getCollege());
        user.setInterest(userWrap.getInterest());
    }
}

package com.vivifram.second.hitalk.manager.chat;

import com.avos.avoscloud.AVCallback;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.FollowCallback;
import com.avos.avoscloud.SaveCallback;
import com.vivifram.second.hitalk.HiTalkApplication;
import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.bean.Constants;
import com.vivifram.second.hitalk.bean.address.AddRequest;
import com.zuowei.dao.greendao.User;
import com.zuowei.utils.bridge.EaterManager;
import com.zuowei.utils.bridge.params.address.UnReadRequestCountParam;
import com.zuowei.utils.common.NLog;
import com.zuowei.utils.common.TagUtil;
import com.zuowei.utils.helper.HiTalkHelper;
import com.zuowei.utils.helper.UserBeanCacheHelper;
import com.zuowei.utils.helper.UserCacheHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import bolts.Continuation;
import bolts.Task;


/**
 * Created by zuowei on 16-10-29.
 */

public class FriendsManager {

    private volatile List<String> friendIds = new ArrayList<>();

    private static FriendsManager friendsManager;

    /**
     * 用户端未读的邀请消息的数量
     */
    private int unreadAddRequestsCount = 0;

    public static synchronized FriendsManager getInstance() {
        if (friendsManager == null) {
            friendsManager = new FriendsManager();
        }
        return friendsManager;
    }

    public FriendsManager() {}

    /**
     * 是否有未读的消息
     */
    public boolean hasUnreadRequests() {
        return unreadAddRequestsCount > 0;
    }

    /**
     * 推送过来时自增
     */
    public void unreadRequestsIncrement() {
        ++ unreadAddRequestsCount;
    }

    /**
     * 从 server 获取未读消息的数量
     */
    public void countUnreadRequests(final CountCallback countCallback) {
        AVQuery<AddRequest> addRequestAVQuery = AVObject.getQuery(AddRequest.class);
        addRequestAVQuery.setCachePolicy(AVQuery.CachePolicy.NETWORK_ONLY);
        addRequestAVQuery.whereEqualTo(AddRequest.TO_USER, HiTalkHelper.getInstance().getCurrentUser());
        addRequestAVQuery.whereEqualTo(AddRequest.IS_READ, false);
        addRequestAVQuery.countInBackground(new CountCallback() {
            @Override
            public void done(int i, AVException e) {
                unreadAddRequestsCount = i;
                EaterManager.getInstance().broadcast(new UnReadRequestCountParam().setUnReadCount(i));
                if (null != countCallback) {
                    countCallback.done(i, e);
                }
            }
        });
    }

    /**
     * 标记消息为已读，标记完后会刷新未读消息数量
     */
    public void markAddRequestsRead(List<AddRequest> addRequestList) {
        if (addRequestList != null) {
            for (AddRequest request : addRequestList) {
                request.put(AddRequest.IS_READ, true);
            }
            AVObject.saveAllInBackground(addRequestList, new SaveCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null) {
                        countUnreadRequests(null);
                    }
                }
            });
        }
    }

    public void findAddRequests(int skip, int limit, FindCallback findCallback) {
        AVUser user = HiTalkHelper.getInstance().getCurrentUser();
        AVQuery<AddRequest> q = AVObject.getQuery(AddRequest.class);
        q.include(AddRequest.FROM_USER);
        q.skip(skip);
        q.limit(limit);
        q.whereEqualTo(AddRequest.TO_USER, user);
        q.orderByDescending(AVObject.CREATED_AT);
        q.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        q.findInBackground(findCallback);
    }

    public void agreeAddRequest(final AddRequest addRequest, final SaveCallback saveCallback) {
        addFriend(addRequest.getFromUser().getObjectId(), new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e != null) {
                    if (e.getCode() == AVException.DUPLICATE_VALUE) {
                        addRequest.setStatus(AddRequest.STATUS_DONE);
                        addRequest.saveInBackground(saveCallback);
                    } else {
                        saveCallback.done(e);
                    }
                } else {
                    addRequest.setStatus(AddRequest.STATUS_DONE);
                    addRequest.saveInBackground(saveCallback);
                }
            }
        });
    }

    public static void addFriend(String friendId, final SaveCallback saveCallback) {
        AVUser user = HiTalkHelper.getInstance().getCurrentUser();
        user.followInBackground(friendId, new FollowCallback() {
            @Override
            public void done(AVObject object, AVException e) {
                if (saveCallback != null) {
                    saveCallback.done(e);
                }
            }
        });
    }

    private void createAddRequest(AVUser toUser) throws Exception {
        AVUser user = HiTalkHelper.getInstance().getCurrentUser();
        AVQuery<AddRequest> q = AVObject.getQuery(AddRequest.class);
        q.whereEqualTo(AddRequest.FROM_USER, user);
        q.whereEqualTo(AddRequest.TO_USER, toUser);
        q.whereEqualTo(AddRequest.STATUS, AddRequest.STATUS_WAIT);
        int count = 0;
        try {
            count = q.count();
        } catch (AVException e) {
            if (e.getCode() == AVException.OBJECT_NOT_FOUND) {
                count = 0;
            } else {
                throw e;
            }
        }
        if (count > 0) {
            // 抛出异常，然后提示用户
            throw new IllegalStateException(HiTalkApplication.mAppContext.
                    getString(R.string.contact_alreadyCreateAddRequest));
        } else {
            AddRequest add = new AddRequest();
            add.setFromUser(user);
            add.setToUser(toUser);
            add.setStatus(AddRequest.STATUS_WAIT);
            add.setIsRead(false);
            add.save();
        }
    }

    public Task<Void> createAddRequestInBackground(final AVUser user) {
        return Task.callInBackground(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                createAddRequest(user);
                return null;
            }
        }).continueWith(new Continuation<Void, Void>() {
            @Override
            public Void then(Task<Void> task) throws Exception {
                if (task.getError() == null) {
                   PushManager.getInstance().pushMessage(user.getObjectId(), HiTalkApplication.mAppContext
                            .getString(R.string.push_add_request),
                            Constants.INVITATION_ACTION);
                }else {
                    throw task.getError();
                }
                return null;
            }
        });
    }

    public List<String> getFriendIds() {
        return friendIds;
    }

    public void setFriendIds(List<String> friendList) {
        friendIds.clear();
        if (friendList != null) {
            friendIds.addAll(friendList);
        }
    }

    public void fetchFriends(boolean isForce, final AVCallback<List<User>> avCallback) {
        AVQuery.CachePolicy policy =
                (isForce ? AVQuery.CachePolicy.NETWORK_ELSE_CACHE : AVQuery.CachePolicy.CACHE_ELSE_NETWORK);
        findFriendsWithCachePolicy(policy, new FindCallback<AVUser>() {
            @Override
            public void done(List<AVUser> list, AVException e) {
                if (null != e) {
                    avCallback.internalDone(null,e);
                } else {
                    final List<String> userIds = new ArrayList<String>();
                    for (AVUser user : list) {
                        userIds.add(user.getObjectId());
                    }
                   /* UserCacheHelper.getInstance().fetchUsers(userIds, (userList, e1) -> {
                        setFriendIds(userIds);
                        findCallback.done(userList, null);
                    });*/
                    UserBeanCacheHelper.getInstance().getCachedUsers(userIds, new AVCallback<List<User>>() {
                        @Override
                        protected void internalDone0(List<User> users, AVException e) {
                            setFriendIds(userIds);
                            if (avCallback != null) {
                                avCallback.internalDone(users,e);
                            }
                        }
                    });
                }
            }
        });
    }

    public void findFriendsWithCachePolicy(AVQuery.CachePolicy cachePolicy, FindCallback<AVUser>
            findCallback) {
        AVQuery<AVUser> q = null;
        try {
            q = HiTalkHelper.getInstance().getCurrentUser().followeeQuery(AVUser.class);
        } catch (Exception e) {
        }
        q.setCachePolicy(cachePolicy);
        q.setMaxCacheAge(TimeUnit.MINUTES.toMillis(1));
        q.findInBackground(findCallback);
    }

}

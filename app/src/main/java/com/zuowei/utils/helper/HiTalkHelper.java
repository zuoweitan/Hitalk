package com.zuowei.utils.helper;

import android.content.Context;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.PaasClient;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.vivifram.second.hitalk.bean.address.AddRequest;
import com.vivifram.second.hitalk.bean.blackboard.BnCommentRemote;
import com.vivifram.second.hitalk.bean.blackboard.BnFavortRemote;
import com.vivifram.second.hitalk.bean.blackboard.BnRemote;
import com.vivifram.second.hitalk.cache.BaseEventuallyQueue;
import com.vivifram.second.hitalk.cache.BeanEventuallyQueue;
import com.vivifram.second.hitalk.cache.HiTalkModel;
import com.vivifram.second.hitalk.manager.chat.PushManager;
import com.vivifram.second.hitalk.state.ActionCallback;
import com.zuowei.utils.bridge.EaterManager;
import com.zuowei.utils.bridge.constant.EaterAction;
import com.zuowei.utils.common.NLog;
import com.zuowei.utils.common.TagUtil;
import com.zuowei.utils.handlers.ClientEventHandler;
import com.zuowei.utils.handlers.ConversationHandler;
import com.zuowei.utils.handlers.MessageHandler;
import com.zuowei.utils.handlers.SharePreferenceHandler;
import com.zuowei.utils.provider.UserProvider;

import static com.zuowei.utils.helper.UserCacheHelper.INSTALLATION;

/**
 * Created by zuowei on 16-7-14.
 * for Hitalk async listener
 */
public class HiTalkHelper {
    public static final String USERNAME = "username";
    private static HiTalkHelper sInstance;
    private static AVUser mCurrentUser;

    private HiTalkHelper(){}

    public static HiTalkHelper getInstance(){
        if (sInstance == null){
            synchronized (HiTalkHelper.class){
                sInstance = new HiTalkHelper();
            }
        }
        return sInstance;
    }

    public static HiTalkHelper $(){
        if (sInstance == null){
            synchronized (HiTalkHelper.class){
                sInstance = new HiTalkHelper();
            }
        }
        return sInstance;
    }

    private static final String TAG = TagUtil.makeTag(HiTalkHelper.class);
    private Context mAppContext;
    private HiTalkModel mHitalkModel;
    private String mUserName;
    private BaseEventuallyQueue eventuallyQueue;

    public void init(Context appContext){
        mAppContext = appContext;
        mHitalkModel = new HiTalkModel(appContext);
        EaterManager.getInstance().registerEater(EaterAction.ACTION_DO_WITH_PREFERENCE,
                SharePreferenceHandler.getInstance().setContext(appContext));
        initRemoteClass();
        initChatkit();
        initPushManager();
        initProviders();

        eventuallyQueue = new BeanEventuallyQueue(appContext);
    }

    private void initPushManager() {
        PushManager.getInstance().init(mAppContext);
    }

    private void initCurrentAVuser(final ActionCallback callback){
        if (mCurrentUser == null) {
            AVQuery<AVUser> query = AVUser.getQuery();
            query.whereEqualTo("objectId",AVUser.getCurrentUser().getObjectId());
            query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
            query.getFirstInBackground(new GetCallback<AVUser>() {
                @Override
                public void done(AVUser avUser, AVException e) {
                    if (avUser != null){
                        mCurrentUser = avUser;
                        callback.onSuccess();
                    }else {
                        mCurrentUser = AVUser.getCurrentUser();
                        callback.onError(0,e.getMessage());
                    }
                }
            });
        }
    }

    private void initRemoteClass() {
        AVObject.registerSubclass(BnRemote.class);
        AVObject.registerSubclass(BnCommentRemote.class);
        AVObject.registerSubclass(BnFavortRemote.class);

        AVObject.registerSubclass(AddRequest.class);
    }

    private void initProviders() {
        UserBeanCacheHelper.getInstance().setUserProvider(new UserProvider());
    }

    private void initChatkit() {
        AVOSCloud.initialize(mAppContext, HiTalkModel.APPID, HiTalkModel.APPKEY);
        AVIMMessageManager.registerMessageHandler(AVIMTypedMessage.class, MessageHandler.getInstance());
        AVIMClient.setClientEventHandler(ClientEventHandler.getInstance());
        AVIMMessageManager.setConversationEventHandler(ConversationHandler.getInstance());
        AVIMClient.setOfflineMessagePush(true);
    }

    public HiTalkModel getModel(){
        return mHitalkModel;
    }

    /**
     * 是否登录成功过
     *
     * @return
     */
    public boolean isLoggedIn() {
        return getCurrentUser() != null;
    }

    public boolean isInfoFull(){
        return getCurrentUser() != null && getCurrentUser().get("college") != null;
    }

    public boolean isMobilePhoneVerified(){
        if (getCurrentUser() != null){
            return getCurrentUser().isMobilePhoneVerified();
        }
        return false;
    }

    public AVUser getCurrentUser(){
        return AVUser.getCurrentUser();
    }

    public void updateUserInfo() {
        AVInstallation installation = AVInstallation.getCurrentInstallation();
        if (installation != null) {
            getCurrentUser().put(INSTALLATION, installation);
            getCurrentUser().saveInBackground();
        }
    }

    /**
     * you can get a user carried with full info.
     * @return
     */
    public AVUser queryCurrentUser(){
        AVQuery<AVUser> query = AVUser.getQuery();
        query.whereEqualTo("objectId",AVUser.getCurrentUser().getObjectId());
        query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        try {
            return query.getFirst();
        } catch (AVException e) {
            NLog.e(TagUtil.makeTag(HiTalkHelper.class),e);
        }
        return getCurrentUser();
    }

    public String getCurrentUserId(){
        AVUser user = getCurrentUser();
        return user != null?user.getObjectId():null;
    }

    public String getCurrentUserCollege(){
        AVUser user = getCurrentUser();
        return user != null? (String) user.get("college") :null;
    }

    public BaseEventuallyQueue getEventuallyQueue() {
        return eventuallyQueue;
    }
}

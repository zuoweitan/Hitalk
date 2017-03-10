package com.zuowei.utils.helper;

import android.text.TextUtils;

import com.vivifram.second.hitalk.cache.ConversationParse;
import com.vivifram.second.hitalk.state.ActionCallback;
import com.zuowei.dao.greendao.Conversation;
import com.zuowei.dao.greendao.ConversationDao;
import com.zuowei.utils.common.RxjavaUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import de.greenrobot.dao.async.AsyncSession;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by zuowei on 16-8-4.
 */
public class ConversationCacheHelper {
    private static ConversationCacheHelper sInstance;
    private Map<String, ConversationParse.Wrap> mCache;
    public static ConversationCacheHelper getInstance(){
        if (sInstance == null) {
            synchronized (ConversationCacheHelper.class){
                if (sInstance == null) {
                    sInstance = new ConversationCacheHelper();
                }
            }
        }
        return sInstance;
    }

    private ConversationCacheHelper(){
        mCache = new HashMap();
    }

    public void init(final ActionCallback callback){
        Observable observable = Observable.just(callback);
        RxjavaUtils.ObServerOnMainThread(
                observable.map(new Func1<Object,Boolean>() {
            @Override
            public Boolean call(Object o) {
                ConversationDao conversationDao = DaoHelper.getInstance().getConversationDao();
                try {
                    List<Conversation> conversations = conversationDao.loadAll();
                    synchronized (mCache){
                        for (int i = 0; i < conversations.size(); i++) {
                            mCache.put(conversations.get(i).getConversationId()
                                    ,ConversationParse.parseFrom(conversations.get(i).getContent()));
                        }
                    }
                    return true;
                }catch (Exception e){
                }
                return false;
            }
        }), new Subscriber() {
            @Override
            public void onCompleted() {
                callback.onSuccess();
            }

            @Override
            public void onError(Throwable e) {
                callback.onError(-1,e.getMessage());
            }

            @Override
            public void onNext(Object o) {
            }
        });
    }

    public void increaseUnreadCount(String convid) {
        if(!TextUtils.isEmpty(convid)) {
            increaseUnreadCount(convid, 1);
        }

    }

    public synchronized void increaseUnreadCount(String convid, int increment) {
        if(!TextUtils.isEmpty(convid) && increment > 0) {
            ConversationParse.Wrap wrap = getConversationItemFromMap(convid);
            wrap.unreadcount += increment;
            syncToCache(wrap);
        }

    }

    public synchronized void clearUnread(String convid) {
        if(!TextUtils.isEmpty(convid)) {
            ConversationParse.Wrap wrap = getConversationItemFromMap(convid);
            wrap.unreadcount = 0;
            syncToCache(wrap);
        }

    }

    public synchronized void deleteConversation(String convid) {
        if(!TextUtils.isEmpty(convid)) {
            ConversationParse.Wrap wrap = mCache.remove(convid);
            deleteFromCache(convid);
        }

    }


    public synchronized void insertConversation(String convid) {
        if(!TextUtils.isEmpty(convid)) {
            ConversationParse.Wrap conversationItemFromMap = getConversationItemFromMap(convid);
            syncToCache(conversationItemFromMap);
        }
    }

    public synchronized int getUnreadCount(String convid) {
        return getConversationItemFromMap(convid).unreadcount;
    }


    public synchronized List<String> getSortedConversationList() {
        ArrayList idList = new ArrayList();
        TreeSet<ConversationParse.Wrap> sortedSet = new TreeSet();
        sortedSet.addAll(mCache.values());
        Iterator<ConversationParse.Wrap> iterator = sortedSet.iterator();

        while(iterator.hasNext()) {
            ConversationParse.Wrap item = iterator.next();
            idList.add(item.conversationId);
        }

        return idList;
    }

    private void syncToCache(ConversationParse.Wrap item) {
        if(null != item) {
            item.updateTime = System.currentTimeMillis();
            mCache.put(item.conversationId, item);
            AsyncSession asyncSession = getAsyncSession();
            asyncSession.insertOrReplace(ConversationParse.toConversationDao(item));
        }
    }

    private void deleteFromCache(ConversationParse.Wrap item){
        if (item != null) {
            mCache.remove(item.conversationId);
            AsyncSession asyncSession = getAsyncSession();
            asyncSession.deleteByKey(item.conversationId);
        }
    }

    private void deleteFromCache(String convid){
        if (convid != null) {
            mCache.remove(convid);
            AsyncSession asyncSession = getAsyncSession();
            asyncSession.deleteByKey(convid);
        }
    }

    private ConversationParse.Wrap getConversationItemFromMap(String convId) {
        return mCache.containsKey(convId)?mCache.get(convId):new ConversationParse.Wrap(convId);
    }


    private AsyncSession getAsyncSession() {
        ConversationDao conversationDao = DaoHelper.getInstance().getConversationDao();
        return DaoHelper.getInstance().wrapDao(conversationDao);
    }
}

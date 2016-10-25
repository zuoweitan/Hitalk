package com.zuowei.utils.helper;

import android.content.Context;

import com.avos.avoscloud.AVCallback;
import com.avos.avoscloud.AVException;
import com.zuowei.dao.greendao.BeanDao;
import com.zuowei.dao.greendao.ConversationDao;
import com.zuowei.dao.greendao.DaoMaster;
import com.zuowei.dao.greendao.DaoSession;
import com.zuowei.dao.greendao.UserDao;
import com.zuowei.utils.common.RxjavaUtils;
import com.zuowei.utils.common.TagUtil;

import java.util.List;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.async.AsyncSession;
import de.greenrobot.dao.query.Query;

/**
 * Created by zuowei on 16-8-2.
 */
public class DaoHelper {
    private static final String TAG = TagUtil.makeTag(DaoHelper.class);
    private static DaoHelper sInstance;
    private Context mCtx;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;
    private AsyncSession mAsyncSession;
    private DaoHelper (){
    }

    public static DaoHelper getInstance(){
        if (sInstance == null){
            synchronized (DaoHelper.class){
                if (sInstance == null){
                    sInstance = new DaoHelper();
                }
            }
        }
        return sInstance;
    }

    public void init(Context ctx){
        mCtx = ctx;
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(ctx,"hitalk-db",null);
        mDaoMaster = new DaoMaster(helper.getWritableDatabase());
        mDaoSession = mDaoMaster.newSession();
        mAsyncSession = mDaoSession.startAsyncSession();
    }

    public UserDao getUserDao(){
        if (mDaoSession != null) {
            return mDaoSession.getUserDao();
        }
        return null;
    }

    public AsyncSession getAsyncSession() {
        return mAsyncSession;
    }

    public AsyncSession wrapDao(AbstractDao abstractDao){
        return new AsyncSession(abstractDao.getSession());
    }

    public <T> void queryAsync(final Query<T> query, final AVCallback<List<T>> callback){
        RxjavaUtils.AsyncJob(new Runnable() {
            @Override
            public void run() {
                try {
                    Query<T> tQuery = query.forCurrentThread();
                    callback.internalDone(tQuery.list(),null);
                }catch (Exception e){
                    callback.internalDone(null,new AVException(new Throwable("queryAsync failed")));
                }


            }
        });
    }

    public ConversationDao getConversationDao(){
        if (mDaoSession != null) {
            return mDaoSession.getConversationDao();
        }
        return null;
    }

    public BeanDao getBeansDao(){
        if (mDaoSession != null) {
            return mDaoSession.getBeanDao();
        }
        return null;
    }

    public void destroy(){
        if (mDaoSession != null) {
            mDaoSession.clear();
        }
    }
}

package com.zuowei.utils.helper;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.google.gson.Gson;
import com.vivifram.second.hitalk.bean.BaseBean;
import com.vivifram.second.hitalk.bean.Constants;
import com.vivifram.second.hitalk.bean.blackboard.BnCommentRemote;
import com.vivifram.second.hitalk.bean.blackboard.BnFavortRemote;
import com.vivifram.second.hitalk.bean.blackboard.BnItem;
import com.vivifram.second.hitalk.bean.blackboard.BnRemote;
import com.vivifram.second.hitalk.bean.blackboard.CommentItem;
import com.vivifram.second.hitalk.bean.blackboard.FavortItem;
import com.vivifram.second.hitalk.cache.BeanEventuallyQueue;
import com.vivifram.second.hitalk.manager.LocalIdManager;
import com.vivifram.second.hitalk.state.DoneCallback;
import com.vivifram.second.hitalk.state.SingleResult;
import com.zuowei.dao.greendao.Bean;
import com.zuowei.dao.greendao.BeanDao;
import com.zuowei.dao.greendao.User;
import com.zuowei.utils.common.NLog;
import com.zuowei.utils.common.TagUtil;
import com.zuowei.utils.common.TaskUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import bolts.Continuation;
import bolts.Task;
import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by zuowei on 16-8-16.
 */
public class BnHelper {
    private final static String TAG = TagUtil.makeTag(BnHelper.class);
    private final String BNPOINTER = "bnPointer";
    private static BnHelper sInstance;
    public static BnHelper getInstance(){
        if (sInstance == null) {
            synchronized (BnHelper.class){
                if (sInstance == null) {
                    sInstance = new BnHelper();
                }
            }
        }
        return sInstance;
    }

    private BnHelper(){
    }

    public void loadMoreBnItemsAsync(final DoneCallback<BnItem> callback, final BnItem lastBnItem){
        BeanEventuallyQueue beanEventuallyQueue = null;
        if (HiTalkHelper.getInstance().getEventuallyQueue() instanceof  BeanEventuallyQueue){
            beanEventuallyQueue = (BeanEventuallyQueue) HiTalkHelper.getInstance().getEventuallyQueue();
        }
        if (beanEventuallyQueue != null) {
            beanEventuallyQueue.requestOneShot();
        }
        Task.callInBackground(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                if (lastBnItem != null) {
                    Task encoder = lastBnItem.getParser().encoder(lastBnItem);
                    TaskUtils.wait(encoder);
                    if (encoder.getError() == null){
                        loadBnDatasInner(loadMoreDataQuery(lastBnItem), (BnRemote) encoder.getResult(),callback);
                    }else {
                        NLog.i(TAG,"loadMoreBnItemsAsync BnItem encoder error");
                    }
                }
                return null;
            }
        });
    }

    private AVQuery<BnRemote> loadMoreDataQuery(BnItem lastBnItem){
        AVQuery<BnRemote> query = AVQuery.getQuery(BnRemote.class);
        initQuery(query);
        query.limit(18);
        query.whereEqualTo("isAll",true);
        query.orderByDescending(BnItem.getCreateTimeFieldName());
        query.include(BnItem.getUserName());
        query.whereLessThan(BnItem.getCreateTimeFieldName(),lastBnItem.getCreateTime());
        return query;
    }

    /**
     * try check eventuallyQueue and upload if it's not empty.
     * @param callback
     */
    public void loadBnItemsAsync(final DoneCallback<BnItem> callback){

        BeanEventuallyQueue beanEventuallyQueue = null;
        if (HiTalkHelper.getInstance().getEventuallyQueue() instanceof  BeanEventuallyQueue){
            beanEventuallyQueue = (BeanEventuallyQueue) HiTalkHelper.getInstance().getEventuallyQueue();
        }
        if (beanEventuallyQueue != null) {
            beanEventuallyQueue.requestOneShot();
        }
        Task.callInBackground(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                loadBnDatasInner(refreshDataQuery(),callback);
                return null;
            }
        });
    }

    private Task<List<BnRemote>> refreshFromLocal(final BnRemote reBnRemoteStart){
        return Task.callInBackground(new Callable<List<BnRemote>>() {
            @Override
            public List<BnRemote> call() throws Exception {
                List<BnRemote> bnRemotes = new ArrayList<BnRemote>();
                BeanDao beanDao = DaoHelper.getInstance().getBeansDao();
                QueryBuilder<Bean> beanQueryBuilder = beanDao.queryBuilder();
                if (reBnRemoteStart == null){
                    beanQueryBuilder.where(BeanDao.Properties.ClassName.eq(BnItem.class.getName()));
                }else {
                    beanQueryBuilder.
                            where(beanQueryBuilder.and(BeanDao.Properties.ClassName.eq(BnItem.class.getName()),
                                    BeanDao.Properties.CreateAt.gt(reBnRemoteStart.getDate(BnItem.getCreateTimeFieldName()))));
                }
                List<Bean> list = beanQueryBuilder.list();
                if (list != null) {
                    for (Bean bean : list){
                        BaseBean baseBean = (BaseBean) new Gson().fromJson(bean.getContent(),Class.forName(bean.getClassName()));
                        Task encoder = baseBean.getParser().encoder(baseBean);
                        TaskUtils.wait(encoder);
                        if (encoder.getError() == null &&
                                encoder.getResult() instanceof  BnRemote){
                            BnRemote result = (BnRemote) encoder.getResult();
                            result.put(BnItem.getIsAllFieldName(),true);
                            bnRemotes.add(result);
                        }
                    }
                }
                return bnRemotes;
            }
        });
    }

    private Task<List<BnRemote>> loadFromLocal(final BnRemote reBnRemoteEnd,final BnRemote reBnRemoteStart){
        return Task.callInBackground(new Callable<List<BnRemote>>() {
            @Override
            public List<BnRemote> call() throws Exception {
                List<BnRemote> bnRemotes = new ArrayList<BnRemote>();
                if (reBnRemoteEnd == null){
                    return bnRemotes;
                }
                BeanDao beanDao = DaoHelper.getInstance().getBeansDao();
                QueryBuilder<Bean> beanQueryBuilder = beanDao.queryBuilder();
                if (reBnRemoteStart == null){
                    beanQueryBuilder.
                            where(beanQueryBuilder.and(BeanDao.Properties.ClassName.eq(BnItem.class.getName()),
                                    BeanDao.Properties.CreateAt.lt(reBnRemoteEnd.getDate(BnItem.getCreateTimeFieldName()))));
                } else {
                    beanQueryBuilder.
                            where(beanQueryBuilder.and(BeanDao.Properties.ClassName.eq(BnItem.class.getName())
                                    ,BeanDao.Properties.CreateAt.gt(reBnRemoteStart.getDate(BnItem.getCreateTimeFieldName()))
                            ,BeanDao.Properties.CreateAt.lt(reBnRemoteEnd.getDate(BnItem.getCreateTimeFieldName()))));
                }
                List<Bean> list = beanQueryBuilder.list();
                if (list != null) {
                    for (Bean bean : list){
                        BaseBean baseBean = (BaseBean) new Gson().fromJson(bean.getContent(),Class.forName(bean.getClassName()));
                        Task encoder = baseBean.getParser().encoder(baseBean);
                        TaskUtils.wait(encoder);
                        if (encoder.getError() == null &&
                                encoder.getResult() instanceof  BnRemote){
                            BnRemote result = (BnRemote) encoder.getResult();
                            result.put(BnItem.getIsAllFieldName(),true);
                            bnRemotes.add(result);
                        }
                    }
                }
                return bnRemotes;
            }
        });
    }

    private AVQuery<BnRemote> refreshDataQuery(){
        AVQuery<BnRemote> query = AVQuery.getQuery(BnRemote.class);
        initQuery(query);
        query.limit(18);
        query.whereEqualTo("isAll",true);
        query.orderByDescending(BnItem.getCreateTimeFieldName());
        query.include(BnItem.getUserName());
        return query;
    }

    private void loadBnDatasInner(final AVQuery<BnRemote> query, final DoneCallback<BnItem> callback){
        loadBnDatasInner(query,null,callback);
    }

    private void loadBnDatasInner(final AVQuery<BnRemote> query,final BnRemote lastBn ,final DoneCallback<BnItem> callback){
        try {
            List<BnRemote> bnRemotes = AvQueryHelper.runQuerySync(query);
            BnRemote reBnRemote = bnRemotes == null ? null:(bnRemotes.size() > 0
                    ? bnRemotes.get(bnRemotes.size() - 1):null);
            Task<List<BnRemote>> listTask = lastBn == null ? refreshFromLocal(reBnRemote) :
                    loadFromLocal(lastBn,reBnRemote);
            TaskUtils.wait(listTask);
            List<BnRemote> localRemotes = listTask.getResult();
            bnRemotes.addAll(localRemotes);
            Collections.sort(bnRemotes, new Comparator<BnRemote>() {
                @Override
                public int compare(BnRemote lhs, BnRemote rhs) {
                    return rhs.getDate(BnItem.getCreateTimeFieldName()).compareTo(
                            lhs.getDate(BnItem.getCreateTimeFieldName()));
                }
            });
            //remove the repeat item
            List<BnRemote> bnRemotesResult = new ArrayList<>();
            BnRemote bnRemote = null;
            BnRemote temp = null;
            for (int i = 0; i < bnRemotes.size(); i++) {
                temp = bnRemotes.get(i);
                if (bnRemote == null || !(bnRemote.getDate(BnItem.getCreateTimeFieldName())
                        .equals(temp.getDate(BnItem.getCreateTimeFieldName())))){
                    bnRemotesResult.add(temp);
                }
                bnRemote = temp;
            }
            loadBnDatasInner(bnRemotesResult,callback);
        } catch (InterruptedException e) {
            callback.done(null,e);
        } catch (AVException e) {
            callback.done(null,e);
        } catch (Exception e){
            callback.done(null,e);
        }
    }
    private void loadBnDatasInner(List<BnRemote> bnRemotes,DoneCallback<BnItem> callback) {

        HashMap<String, List<CommentItem>> localCommentMap= requestLocalComment();
        HashMap<String, List<FavortItem>> localFavortMap= requestLocalFavort();

        ArrayList<BnItem> bnItems = new ArrayList<BnItem>();
        if (bnRemotes != null) {
            for (BnRemote remote : bnRemotes) {
                if (!isComplete(remote))
                    continue;
                BnItem bnItem = new BnItem();
                bnItem.setId(remote.getObjectId());
                bnItem.setContent(remote.getString(BnItem.getContentFieldName()));
                bnItem.setLinkImg(remote.getString(BnItem.getLinkImgFieldName()));
                bnItem.setLinkTitle(remote.getString(BnItem.getLinkTitleFieldName()));
                if (remote.has(BnItem.getCreateTimeFieldName())) {
                    bnItem.setCreateTime(remote.getDate(BnItem.getCreateTimeFieldName()));
                } else {
                    bnItem.setCreateTime(remote.getCreatedAt());
                }
                User user = new User();
                AVUser avUser = remote.getAVUser(BnItem.getUserName());
                if (avUser != null) {
                    UserBeanCacheHelper.AvUserToUser(avUser, user);
                    bnItem.setUser(user);
                }
                bnItem.setPhotos(toPhotos(remote.getMap(BnItem.getPhotosFieldName())));
                bnItem.setType(remote.getString(BnItem.getTypeFieldName()));
                bnItem.setVideoUrl(remote.getString(BnItem.getVideoUrlFieldName()));
                bnItem.setVideoImgUrl(remote.getString(BnItem.getVideoImgUrlFieldName()));

                //first check comment
                AVQuery<BnCommentRemote> bcRemoteQuery= AVQuery.getQuery(BnCommentRemote.class);
                initQuery(bcRemoteQuery);
                bcRemoteQuery.include(CommentItem.getUserFieldName());
                bcRemoteQuery.include(CommentItem.getToReplyUserFieldName());
                bcRemoteQuery.whereEqualTo(BnItem.asBnTargetName(),remote);

                try {
                    List<CommentItem> localComments = localCommentMap.get(bnItem.getId());
                    List<BnCommentRemote> bnCommentRemotes = AvQueryHelper.runQuerySync(bcRemoteQuery);
                    ArrayList<CommentItem> commentItems = new ArrayList<CommentItem>();
                    if (bnCommentRemotes != null) {
                        for (BnCommentRemote commentRemote : bnCommentRemotes) {
                            boolean isValid = StaticDataCacheHelper.getInstance().checkValid(commentRemote.getObjectId());
                            if (isValid) {
                                CommentItem commentItem = new CommentItem();
                                commentItem.setBnRemoteId(remote.getObjectId());
                                remoteToComment(commentRemote, commentItem);
                                commentItems.add(commentItem);
                            }
                        }
                    }
                    if (localComments != null) {
                        commentItems.addAll(localComments);
                    }
                    Collections.sort(commentItems, new Comparator<CommentItem>() {
                        @Override
                        public int compare(CommentItem lhs, CommentItem rhs) {
                            return lhs.getCreateTime().compareTo(
                                    rhs.getCreateTime());
                        }
                    });
                    bnItem.setComments(commentItems);
                    if (commentItems.size() > 0) {
                        bnItem.setHasComment(true);
                    }
                }catch (Exception e){
                    NLog.i(TAG,"fetchRemotes failed e : ",e);
                }

                //then check favort
                AVQuery<BnFavortRemote> bfRemoteQuery = AVQuery.getQuery(BnFavortRemote.class);
                initQuery(bfRemoteQuery);
                bfRemoteQuery.include(FavortItem.getUserFieldName());
                bfRemoteQuery.whereEqualTo(BnItem.asBnTargetName(),remote);
                try {
                    List<FavortItem> localFavorts = localFavortMap.get(bnItem.getId());
                    List<BnFavortRemote> bnFavortRemotes = AvQueryHelper.runQuerySync(bfRemoteQuery);

                    ArrayList<FavortItem> favortItems = new ArrayList<FavortItem>();
                    if (bnFavortRemotes != null){
                        for (BnFavortRemote favortRemote : bnFavortRemotes) {
                            if (StaticDataCacheHelper.getInstance().checkValid(favortRemote.getObjectId())) {
                                FavortItem favortItem = new FavortItem();
                                favortItem.setBnRemoteId(remote.getObjectId());
                                remoteToFavort(favortRemote, favortItem);
                                favortItems.add(favortItem);
                            }
                        }
                    }
                    if (localFavorts != null) {
                        favortItems.addAll(localFavorts);
                    }
                    Collections.sort(favortItems, new Comparator<FavortItem>() {
                        @Override
                        public int compare(FavortItem lhs, FavortItem rhs) {
                            return lhs.getCreateTime().compareTo(
                                    rhs.getCreateTime());
                        }
                    });
                    bnItem.setFavorters(favortItems);
                    if (favortItems.size() > 0) {
                        bnItem.setHasFavort(true);
                    }
                }catch (Exception e){
                    NLog.i(TAG,"fetchRemotes failed e : ",e);
                }

                bnItems.add(bnItem);
            }
            callback.done(bnItems,null);
        }else {
            callback.done(new ArrayList<BnItem>(),null);
        }
    }

    private HashMap<String,List<CommentItem>> requestLocalComment(){
        HashMap<String,List<CommentItem>> result = new HashMap<>();
        BeanDao beanDao = DaoHelper.getInstance().getBeansDao();
        QueryBuilder<Bean> beanQueryBuilder = beanDao.queryBuilder();
        beanQueryBuilder.where(beanQueryBuilder.and(BeanDao.Properties.ClassName.eq(CommentItem.class.getName()),
                BeanDao.Properties.CommandType.notEq(BaseBean.TYPE_DELETE)));
        List<Bean> list = beanQueryBuilder.list();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                Bean bean = list.get(i);
                try {
                    BaseBean baseBean = (BaseBean) new Gson().fromJson(bean.getContent(),Class.forName(bean.getClassName()));
                    if (baseBean instanceof CommentItem){
                        CommentItem commentItem = (CommentItem) baseBean;
                        List<CommentItem> commentItems = result.get(commentItem.getBnRemoteId());
                        if (commentItems == null){
                            commentItems = new ArrayList<>();
                            commentItems.add(commentItem);
                            result.put(commentItem.getBnRemoteId(),commentItems);
                        }else {
                            commentItems.add(commentItem);
                        }
                    }
                }catch (Exception e){
                    NLog.i(TAG,"requestLocalComment a error occur :",e);
                }
            }
        }
        return result;
    }

    private HashMap<String,List<FavortItem>> requestLocalFavort(){
        HashMap<String,List<FavortItem>> result = new HashMap<>();
        BeanDao beanDao = DaoHelper.getInstance().getBeansDao();
        QueryBuilder<Bean> beanQueryBuilder = beanDao.queryBuilder();
        beanQueryBuilder.where(beanQueryBuilder.and(BeanDao.Properties.ClassName.eq(FavortItem.class.getName()),
                BeanDao.Properties.CommandType.notEq(BaseBean.TYPE_DELETE)));
        List<Bean> list = beanQueryBuilder.list();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                Bean bean = list.get(i);
                try {
                    BaseBean baseBean = (BaseBean) new Gson().fromJson(bean.getContent(),Class.forName(bean.getClassName()));
                    if (baseBean instanceof FavortItem){
                        FavortItem favortItem = (FavortItem) baseBean;
                        List<FavortItem> favortItems = result.get(favortItem.getBnRemoteId());
                        if (favortItems == null){
                            favortItems = new ArrayList<>();
                            favortItems.add(favortItem);
                            result.put(favortItem.getBnRemoteId(),favortItems);
                        }else {
                            favortItems.add(favortItem);
                        }
                    }
                }catch (Exception e){
                    NLog.i(TAG,"requestLocalFavort a error occur :",e);
                }
            }
        }
        return result;
    }

    private HashMap<String, String> toPhotos(Map<String, Object> map) {
        if (map == null){
            return null;
        }
        HashMap<String,String> photos = new HashMap<>();
        try {
            for (Map.Entry<String,Object> entry : map.entrySet()){
                photos.put(Constants.BN_PHOTOS_DESUTIL.decrypt(entry.getKey()),
                        Constants.BN_PHOTOS_DESUTIL.decrypt((String) entry.getValue()));
            }
        }catch (Exception e){

        }
        return photos;
    }

    private boolean isComplete(BnRemote remote) {
        if (remote.has(BnItem.getIsAllFieldName())){
            return remote.getBoolean(BnItem.getIsAllFieldName());
        }
        return true;
    }

    public void saveBnItemAsync(final BnItem bnItem, final SingleResult callback){
        if (bnItem == null){
            callback.done(null,new NullPointerException("bnItem is null"));
            return;
        }
        bnItem.setSaveListener(new BaseBean.ISaveListener() {
            @Override
            public void onSaveCompleted(boolean success) {
                if (callback != null) {
                    if (success) {
                        callback.done(null, null);
                    }else {
                        callback.done(null,new Exception("bnItem save failed"));
                    }
                }
            }
        });
        bnItem.save().continueWith(new Continuation<BnRemote, Object>() {
            @Override
            public Object then(Task<BnRemote> task) throws Exception {
                Exception e = task.getError();
                NLog.i(TAG,"saveBnItemAsync e = "+e);
                if (e != null) {
                    HiTalkHelper.getInstance().getEventuallyQueue().enqueueEventuallyAsync(bnItem);
                }
                return null;
            }
        });
    }

    public void saveCommentAsync(final CommentItem comment, String bnId){
        if (LocalIdManager.getInstance().isLocalId(bnId)){

        }else {
            comment.setBnRemoteId(bnId);
            comment.save().continueWith(new Continuation<BnCommentRemote, Object>() {
                @Override
                public Object then(Task<BnCommentRemote> task) throws Exception {
                    Exception e = task.getError();
                    NLog.i(TAG,"saveCommentAsync e = "+e);
                    if (e != null) {
                        HiTalkHelper.getInstance().getEventuallyQueue().enqueueEventuallyAsync(comment);
                    }
                    return null;
                }
            });
        }
    }

    public void saveFavortAsync(final FavortItem favortItem, String bnId){
        if (LocalIdManager.getInstance().isLocalId(bnId)){

        }else {
            favortItem.setBnRemoteId(bnId);
            favortItem.save().continueWith(new Continuation<BnFavortRemote, Object>() {
                @Override
                public Object then(Task<BnFavortRemote> task) throws Exception {
                    Exception e = task.getError();
                    if (e != null) {
                        HiTalkHelper.getInstance().getEventuallyQueue().enqueueEventuallyAsync(favortItem);
                    }
                    return null;
                }
            });
        }
    }

    public static void remoteToComment(BnCommentRemote remote, CommentItem commentItem){
        commentItem.setId(remote.getObjectId());
        if (remote.has(CommentItem.getContentFieldName())){
            commentItem.setContent(remote.getString(CommentItem.getContentFieldName()));
        }

        if (remote.has(CommentItem.getUserFieldName())){
            User user = new User();
            AVUser avUser;
            if ((avUser = remote.getAVUser(CommentItem.getUserFieldName())) != null) {
                UserBeanCacheHelper.AvUserToUser(avUser, user);
                commentItem.setUser(user);
            }
        }

        if (remote.has(CommentItem.getToReplyUserFieldName())){
            User user = new User();
            AVUser avUser;
            if ((avUser = remote.getAVUser(CommentItem.getToReplyUserFieldName())) != null) {
                UserBeanCacheHelper.AvUserToUser(avUser, user);
                commentItem.setToReplyUser(user);
            }
        }

        if (remote.has(CommentItem.getCreateTimeFieldName())){
            commentItem.setCreateTime(remote.getDate(CommentItem.getCreateTimeFieldName()));
        }else {
            commentItem.setCreateTime(remote.getCreatedAt());
        }
    }

    public static void remoteToFavort(BnFavortRemote remote,FavortItem favortItem){
        favortItem.setId(remote.getObjectId());
        if (remote.has(FavortItem.getUserFieldName())){
            User user = new User();
            AVUser avUser;
            if ((avUser = remote.getAVUser(FavortItem.getUserFieldName())) != null) {
                UserBeanCacheHelper.AvUserToUser(avUser, user);
                favortItem.setUser(user);
            }
        }

        if (remote.has(FavortItem.getCreateTimeFieldName())){
            favortItem.setCreateTime(remote.getDate(FavortItem.getCreateTimeFieldName()));
        }else {
            favortItem.setCreateTime(remote.getCreatedAt());
        }
    }

    private boolean filterException(AVException e) {
        if (e != null){
            NLog.e(TagUtil.makeTag(BnHelper.class),e);
            return false;
        }
        return true;
    }


    private void initQuery(AVQuery avQuery){
        avQuery.setMaxCacheAge(30 * 24 * 60 * 60);
        avQuery.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
    }

}

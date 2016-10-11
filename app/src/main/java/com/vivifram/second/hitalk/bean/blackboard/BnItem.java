package com.vivifram.second.hitalk.bean.blackboard;

import android.text.TextUtils;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.lzy.ninegrid.ImageInfo;
import com.vivifram.second.hitalk.bean.BaseBean;
import com.vivifram.second.hitalk.bean.parser.BeanParser;
import com.vivifram.second.hitalk.bean.parser.BnItemParser;
import com.vivifram.second.hitalk.bean.parser.ParserFactory;
import com.vivifram.second.hitalk.manager.LocalIdManager;
import com.zuowei.dao.greendao.Bean;
import com.zuowei.dao.greendao.User;
import com.zuowei.utils.common.NLog;
import com.zuowei.utils.common.TagUtil;
import com.zuowei.utils.helper.AvObjectSaveHelper;
import com.zuowei.utils.helper.BeanHelper;
import com.zuowei.utils.helper.FileStorageHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import bolts.Continuation;
import bolts.Task;


public class BnItem extends BaseBean {

    public final static String TYPE_URL = "1";
    public final static String TYPE_IMG = "2";
    public final static String TYPE_VIDEO = "3";
    public final static String TYPE_TEXT = "4";

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String id;
    private String content;
    private String type;//1:链接  2:图片 3:视频
    private String linkImg;
    private String linkTitle;
    private HashMap<String,String> photos;
    private List<FavortItem> favorters;
    private List<CommentItem> comments;
    private User user;
    private String videoUrl;
    private String videoImgUrl;
    private boolean hasFavort;
    private boolean hasComment;
    private boolean isAll;

    public void readFrom(BnItem bnItem) {
        id = bnItem.id;
        content = bnItem.content;
        type = bnItem.type;
        linkImg = bnItem.linkImg;
        linkTitle = bnItem.linkTitle;
        photos = bnItem.photos;
        favorters = bnItem.favorters;
        comments = bnItem.comments;
        user = bnItem.user;
        videoImgUrl = bnItem.videoImgUrl;
        videoUrl = bnItem.videoUrl;
        hasFavort = bnItem.hasFavort;
        hasComment = bnItem.hasComment;
        setCreateTime(bnItem.getCreateTime());
        setLocalId(getId());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        setLocalId(getId());
    }

    public String getContent() {
        return content;
    }

    public static String getContentFieldName(){
        return "content";
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public static String getTypeFieldName(){
        return "type";
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<FavortItem> getFavorters() {
        if (favorters == null){
            favorters = new ArrayList<>();
        }
        return favorters;
    }

    public static String getFavortersFieldName(){
        return "favorters";
    }

    public void setFavorters(List<FavortItem> favorters) {
        this.favorters = favorters;
    }

    public List<CommentItem> getComments() {
        if (comments == null){
            comments = new ArrayList<>();
        }
        return comments;
    }

    public static String getCommentsFieldName(){
        return "comments";
    }

    public void setComments(List<CommentItem> comments) {
        this.comments = comments;
    }

    public String getLinkImg() {
        return linkImg;
    }

    public static String getLinkImgFieldName(){
        return "linkImg";
    }

    public void setLinkImg(String linkImg) {
        this.linkImg = linkImg;
    }

    public String getLinkTitle() {
        return linkTitle;
    }

    public static String getLinkTitleFieldName(){
        return "linkTitle";
    }

    public void setLinkTitle(String linkTitle) {
        this.linkTitle = linkTitle;
    }

    public HashMap<String, String> getPhotos() {
        return photos;
    }

    public static String getPhotosFieldName(){
        return "photos";
    }

    public void setPhotos(HashMap<String,String> photos) {
        this.photos = photos;
    }

    public User getUser() {
        return user;
    }

    public static String getUserName(){
        return "user";
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public static String getVideoUrlFieldName(){
        return "videoUrl";
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getVideoImgUrl() {
        return videoImgUrl;
    }

    public void setVideoImgUrl(String videoImgUrl) {
        this.videoImgUrl = videoImgUrl;
    }

    public static String getVideoImgUrlFieldName(){
        return "videoImgUrl";
    }

    public void setHasFavort(boolean hasFavort) {
        this.hasFavort = hasFavort;
    }
    public static String getHasFavortFieldName(){
        return "hasFavort";
    }

    public boolean hasFavort() {
        return hasFavort;
    }

    public void setHasComment(boolean hasComment) {
        this.hasComment = hasComment;
    }

    public static String getHasCommentFieldName(){
        return "hasComment";
    }

    public boolean hasComment() {
        return hasComment;
    }

    public String getCurUserFavortId(String curUserId) {
        String favortid = "";
        if (!TextUtils.isEmpty(curUserId) && hasFavort()) {
            for (FavortItem item : favorters) {
                if (curUserId.equals(item.getUser().getObjectId())) {
                    favortid = item.getId();
                    return favortid;
                }
            }
        }
        return favortid;
    }

    public FavortItem getFavortItemById(String id){
        if (favorters != null){
            for (FavortItem item : favorters){
                if (item.getId().equals(id)){
                    return item;
                }
            }
        }
        return null;
    }

    public CommentItem getCommentItemById(String id){
        if (comments != null){
            for (CommentItem item : comments){
                if (item.getId().equals(id)){
                    return item;
                }
            }
        }
        return null;
    }

    public static String asBnTargetName(){
        return "targetBn";
    }

    @Override
    public BeanParser getParser() {
        return ParserFactory.getParser(this);
    }

    public Task<BnRemote> simpleSave(){

        Task<BnRemote> tempSave = getParser().encoder(this)
                .continueWithTask(new Continuation<AVObject, Task<BnRemote>>() {
                    @Override
                    public Task<BnRemote> then(Task<AVObject> task) throws Exception {

                        AVObject avObject = task.getResult();
                        Exception e = task.getError();
                        if (e != null) {
                            return Task.forError(e);
                        }
                        if (avObject == null || !(avObject instanceof BnRemote)){
                            return Task.forError(new Exception("avObject is not instanceof BnRemote"));
                        }
                        BnRemote bnRemote = (BnRemote) avObject;
                        try {
                            bnRemote.save();
                        }catch (AVException ave){
                            return Task.forError(ave);
                        }
                        setId(bnRemote.getObjectId());
                        return Task.forResult(bnRemote);
                    }
                });

        return tempSave;
    }

    @Override
    public Task<BnRemote> save() {
        return getParser().encoder(this).continueWithTask(new Continuation<AVObject, Task<BnRemote>>() {
            @Override
            public Task<BnRemote> then(Task<AVObject> task) throws Exception {
                NLog.i(TagUtil.makeTag(BnItem.class),"run on thread "+Thread.currentThread().getName());
                AVObject avObject = task.getResult();
                Exception e = task.getError();
                boolean saveSuccess = false;
                if (e != null) {
                    notifyListener(saveSuccess);
                    NLog.i(TagUtil.makeTag(BnItem.class),"parser e = "+e);
                    return Task.forError(e);
                }
                if (avObject == null || !(avObject instanceof BnRemote)){
                    notifyListener(saveSuccess);
                    return Task.forError(new Exception("avObject is not instanceof BnRemote"));
                }
                BnRemote bnRemote = (BnRemote) avObject;
                if (LocalIdManager.getInstance().isLocalId(getId())){
                    try {
                        AvObjectSaveHelper.runSaveSync(bnRemote);
                    }catch (Exception ave){
                        NLog.i(TagUtil.makeTag(BnItem.class),"bnRemote save e = "+ave);
                        saveSuccess = false;
                        notifyListener(saveSuccess);
                        return Task.forError(ave);
                    }
                    setId(bnRemote.getObjectId());
                    final BnRemote finalBnRemote = bnRemote;
                    return BeanHelper.saveBean(BnItem.this).continueWithTask(new Continuation<Bean, Task<BnRemote>>() {
                        @Override
                        public Task<BnRemote> then(Task<Bean> task) throws Exception {
                            Exception e = task.getError();
                            if (e == null){
                                notifyListener(false);
                                return Task.forResult(finalBnRemote);
                            }
                            notifyListener(true);
                            return Task.forError(e);
                        }
                    });
                }else {
                    bnRemote = AVObject.createWithoutData(BnRemote.class,getId());
                    return Task.forResult(bnRemote);
                }
            }
        },Task.BACKGROUND_EXECUTOR).onSuccessTask(new Continuation<BnRemote, Task<BnRemote>>() {
            @Override
            public Task<BnRemote> then(Task<BnRemote> task) throws Exception {
                final BnRemote bnRemote = task.getResult();

                NLog.i(TagUtil.makeTag(getClass()),"do save comments and favorts");
                //save comment
                List<Task<BnCommentRemote>> crtasks = new ArrayList<Task<BnCommentRemote>>();
                if (hasComment()){
                    for (CommentItem commentItem : getComments()){
                        commentItem.setBnRemoteId(bnRemote.getObjectId());
                        crtasks.add(commentItem.save());
                    }
                }

                //save favort
                final List<Task<BnFavortRemote>> fvtasks = new ArrayList<Task<BnFavortRemote>>();
                if (hasFavort()){
                    for (FavortItem favortItem : getFavorters()){
                        favortItem.setBnRemoteId(bnRemote.getObjectId());
                        fvtasks.add(favortItem.save());
                    }
                }

                return Task.whenAll(crtasks).onSuccessTask(new Continuation<Void, Task<Void>>() {
                    @Override
                    public Task<Void> then(Task<Void> task) throws Exception {
                        return Task.whenAll(fvtasks).makeVoid();
                    }
                }).onSuccessTask(new Continuation<Void, Task<BnRemote>>() {
                    @Override
                    public Task<BnRemote> then(Task<Void> task) throws Exception {
                        return Task.forResult(bnRemote);
                    }
                });
            }
        }).onSuccessTask(new Continuation<BnRemote, Task<BnRemote>>() {
            @Override
            public Task<BnRemote> then(Task<BnRemote> task) throws Exception {
                NLog.i(TagUtil.makeTag(getClass()),"do save images and photos = "+getPhotos());
                List<String> photos = new ArrayList<String>();
                if (getPhotos() != null){
                    photos.addAll(getPhotos().keySet());
                }
                final BnRemote bnRemote = task.getResult();
                return FileStorageHelper.getInstance().uploadImagesTask(photos).
                        continueWithTask(new Continuation<List<ImageInfo>, Task<BnRemote>>() {
                    @Override
                    public Task<BnRemote> then(Task<List<ImageInfo>> task) throws Exception {
                        Exception e = task.getError();
                        NLog.i(TagUtil.makeTag(getClass()),"file save and e = "+ e);
                        if (e != null) {
                            return Task.forError(e);
                        }
                        setAll(true);
                        List<ImageInfo> imageInfos = task.getResult();
                        HashMap<String,String> photos = new HashMap<String, String>();
                        for (ImageInfo imageInfo : imageInfos) {
                            photos.put(imageInfo.getBigImageUrl(),imageInfo.getThumbnailUrl());
                        }
                        return Task.forResult(photos).continueWithTask(new Continuation<HashMap<String, String>, Task<BnRemote>>() {
                            @Override
                            public Task<BnRemote> then(Task<HashMap<String, String>> task) throws Exception {
                                HashMap<String,String> photosTemp = task.getResult();
                                bnRemote.put(getPhotosFieldName(), BnItemParser.toRemoteHashMap(photosTemp));
                                bnRemote.put(getIsAllFieldName(),isAll());
                                try {
                                    AvObjectSaveHelper.runSaveSync(bnRemote);
                                }catch (Exception e){
                                    return Task.forError(e);
                                }

                                return Task.forResult(bnRemote);
                            }
                        });
                    }
                });
            }
        }).onSuccessTask(new Continuation<BnRemote, Task<BnRemote>>() {
            @Override
            public Task<BnRemote> then(Task<BnRemote> task) throws Exception {
                BeanHelper.deleteBean(getLocalId());
                return task;
            }
        });
    }

    private void notifyListener(boolean saveSuccess) {
        if (getSaveListener() != null) {
            getSaveListener().onSaveCompleted(saveSuccess);
        }
    }

    public void setAll(boolean all) {
        isAll = all;
    }

    public boolean isAll() {
        return isAll;
    }

    public static String getIsAllFieldName(){
        return "isAll";
    }
}

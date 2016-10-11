package com.vivifram.second.hitalk.bean.blackboard;

import com.avos.avoscloud.AVObject;
import com.vivifram.second.hitalk.bean.BaseBean;
import com.vivifram.second.hitalk.bean.parser.BeanParser;
import com.vivifram.second.hitalk.bean.parser.ParserFactory;
import com.vivifram.second.hitalk.manager.LocalIdManager;
import com.zuowei.dao.greendao.Bean;
import com.zuowei.dao.greendao.User;
import com.zuowei.utils.helper.AvObjectSaveHelper;
import com.zuowei.utils.helper.BeanHelper;
import com.zuowei.utils.helper.StaticDataCacheHelper;

import bolts.Continuation;
import bolts.Task;

public class CommentItem extends BaseBean {

    private String id;
    private User user;
    private User toReplyUser;
    private String content;
    private String bnRemoteId;

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

    public User getUser() {
        return user;
    }

    public static String getUserFieldName(){
        return "user";
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getToReplyUser() {
        return toReplyUser;
    }

    public static String getToReplyUserFieldName(){
        return "toReplyUser";
    }

    public void setToReplyUser(User toReplyUser) {
        this.toReplyUser = toReplyUser;
    }

    @Override
    public String toString() {
        return "CommentItem{" +
                "id='" + id + '\'' +
                ", user=" + user +
                ", toReplyUser=" + toReplyUser +
                ", content='" + content + '\'' +
                ", bnRemoteId='" + bnRemoteId + '\'' +
                '}';
    }

    @Override
    public BeanParser getParser() {
        return ParserFactory.getParser(this);
    }

    @Override
    public Task<BnCommentRemote> save() {
        return getParser().encoder(this).continueWithTask(new Continuation<AVObject, Task<BnCommentRemote>>() {
            @Override
            public Task<BnCommentRemote> then(Task<AVObject> task) throws Exception {
                Exception exception = task.getError();
                AVObject avObject = task.getResult();
                if (exception != null) {
                    return Task.forError(exception);
                }
                if (avObject == null || !(avObject instanceof BnCommentRemote)){
                    return Task.forError(new Exception("avObject is not instanceof BnCommentRemote"));
                }
                BnCommentRemote bnCommentRemote = (BnCommentRemote) avObject;
                if (LocalIdManager.getInstance().isLocalId(getId())){
                    try {
                        AvObjectSaveHelper.runSaveSync(bnCommentRemote);
                    }catch (Exception ave){
                        return Task.forError(ave);
                    }
                    setId(bnCommentRemote.getObjectId());
                    final BnCommentRemote finalBnCommentRemote = bnCommentRemote;
                    return BeanHelper.saveBean(CommentItem.this).continueWithTask(new Continuation<Bean, Task<BnCommentRemote>>() {
                        @Override
                        public Task<BnCommentRemote> then(Task<Bean> task) throws Exception {
                            Exception e = task.getError();
                            if (e == null){
                                return Task.forResult(finalBnCommentRemote);
                            }
                            return Task.forError(e);
                        }
                    });
                }else {
                    bnCommentRemote = AVObject.createWithoutData(BnCommentRemote.class,getId());
                    return Task.forResult(bnCommentRemote);
                }
            }
        },Task.BACKGROUND_EXECUTOR).onSuccessTask(new Continuation<BnCommentRemote,Task<BnCommentRemote>>() {
            @Override
            public Task<BnCommentRemote> then(Task<BnCommentRemote> task) throws Exception {
                BeanHelper.deleteBean(getLocalId());
                return task;
            }
        });
    }

    @Override
    public <T extends AVObject> Task<T> delete() {
        return getParser().encoder(this).continueWithTask(new Continuation<AVObject, Task<BnCommentRemote>>() {
            @Override
            public Task<BnCommentRemote> then(Task<AVObject> task) throws Exception {
                Exception exception = task.getError();
                AVObject avObject = task.getResult();
                if (exception != null) {
                    return Task.forError(exception);
                }
                if (avObject == null || !(avObject instanceof BnCommentRemote)){
                    return Task.forError(new Exception("avObject is not instanceof BnCommentRemote"));
                }
                StaticDataCacheHelper.getInstance().setItemValid(getId(),1);
                BnCommentRemote bnCommentRemote = (BnCommentRemote) avObject;
                if (LocalIdManager.getInstance().isLocalId(getId())){
                    switch (getCommandType()){
                        case TYPE_SAVE:
                            setCommandType(TYPE_DELETE);
                            BeanHelper.saveBean(CommentItem.this);
                            break;
                        case TYPE_DELETE:
                            BeanHelper.deleteBean(getLocalId());
                            break;
                    }
                }else {
                    bnCommentRemote = AVObject.createWithoutData(BnCommentRemote.class,getId());
                    try {
                        bnCommentRemote.delete();
                    }catch (Exception e){
                        return Task.forError(e);
                    }
                }
                return Task.forResult(bnCommentRemote);
            }
        },Task.BACKGROUND_EXECUTOR).onSuccessTask(new Continuation() {
            @Override
            public Object then(Task task) throws Exception {
                StaticDataCacheHelper.getInstance().setItemValid(getId(),0);
                return null;
            }
        });
    }

    public String getBnRemoteId() {
        return bnRemoteId;
    }

    public void setBnRemoteId(String bnRemoteId) {
        this.bnRemoteId = bnRemoteId;
    }
}

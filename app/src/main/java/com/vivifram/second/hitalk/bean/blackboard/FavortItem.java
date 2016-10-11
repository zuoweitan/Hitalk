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

public class FavortItem extends BaseBean {

    private static final long serialVersionUID = 1L;
    private String id;
    private User user;
    private String bnRemoteId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        setLocalId(getId());
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

    @Override
    public BeanParser getParser() {
        return ParserFactory.getParser(this);
    }

    @Override
    public Task<BnFavortRemote> save() {
        return getParser().encoder(this).continueWithTask(new Continuation<AVObject, Task<BnFavortRemote>>() {
            @Override
            public Task<BnFavortRemote> then(Task<AVObject> task) throws Exception {
                Exception exception = task.getError();
                AVObject avObject = task.getResult();
                if (exception != null) {
                    return Task.forError(exception);
                }
                if (avObject == null || !(avObject instanceof BnFavortRemote)){
                    return Task.forError(new Exception("avObject is not instanceof BnFavortRemote"));
                }

                BnFavortRemote bnFavortRemote = (BnFavortRemote) avObject;
                if (LocalIdManager.getInstance().isLocalId(getId())) {
                    try {
                        AvObjectSaveHelper.runSaveSync(bnFavortRemote);
                    } catch (Exception ave) {
                        return Task.forError(ave);
                    }

                    setId(bnFavortRemote.getObjectId());
                    final BnFavortRemote finalBnFavortRemote = bnFavortRemote;
                    return BeanHelper.saveBean(FavortItem.this).continueWithTask(new Continuation<Bean, Task<BnFavortRemote>>() {
                        @Override
                        public Task<BnFavortRemote> then(Task<Bean> task) throws Exception {
                            Exception e = task.getError();
                            if (e == null){
                                return Task.forResult(finalBnFavortRemote);
                            }
                            return Task.forError(e);
                        }
                    });
                }else {
                    bnFavortRemote = AVObject.createWithoutData(BnFavortRemote.class,getId());
                    return Task.forResult(bnFavortRemote);
                }
            }
        },Task.BACKGROUND_EXECUTOR).onSuccessTask(new Continuation<BnFavortRemote,Task<BnFavortRemote>>() {
            @Override
            public Task<BnFavortRemote> then(Task<BnFavortRemote> task) throws Exception {
                BeanHelper.deleteBean(getLocalId());
                return task;
            }
        });
    }

    @Override
    public <T extends AVObject> Task<T> delete() {
        return getParser().encoder(this).continueWithTask(new Continuation<AVObject, Task<BnFavortRemote>>() {
            @Override
            public Task<BnFavortRemote> then(Task<AVObject> task) throws Exception {
                Exception exception = task.getError();
                AVObject avObject = task.getResult();
                if (exception != null) {
                    return Task.forError(exception);
                }
                if (avObject == null || !(avObject instanceof BnFavortRemote)){
                    return Task.forError(new Exception("avObject is not instanceof BnFavortRemote"));
                }
                StaticDataCacheHelper.getInstance().setItemValid(getId(),1);
                BnFavortRemote bnFavortRemote = (BnFavortRemote) avObject;
                if (LocalIdManager.getInstance().isLocalId(getId())) {
                    switch (getCommandType()){
                        case TYPE_SAVE:
                            setCommandType(TYPE_DELETE);
                            BeanHelper.saveBean(FavortItem.this);
                            break;
                        case TYPE_DELETE:
                            BeanHelper.deleteBean(getLocalId());
                            break;
                    }
                }else {
                    bnFavortRemote = AVObject.createWithoutData(BnFavortRemote.class,getId());
                    try {
                        bnFavortRemote.delete();
                    }catch (Exception e){
                        return Task.forError(e);
                    }
                }
                return Task.forResult(bnFavortRemote);
            }
        },Task.BACKGROUND_EXECUTOR)
                .onSuccessTask(new Continuation() {
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

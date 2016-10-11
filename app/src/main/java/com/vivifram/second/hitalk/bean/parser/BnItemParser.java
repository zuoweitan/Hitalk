package com.vivifram.second.hitalk.bean.parser;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.vivifram.second.hitalk.bean.DesConstant;
import com.vivifram.second.hitalk.bean.blackboard.BnItem;
import com.vivifram.second.hitalk.bean.blackboard.BnRemote;
import com.zuowei.utils.common.NLog;
import com.zuowei.utils.common.TagUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import bolts.Task;

/**
 * Created by zuowei on 16-8-24.
 */
public class BnItemParser extends BeanParser<BnItem> {
    @Override
    public Task<AVObject> encoder(final BnItem bnItem) {
        return Task.callInBackground(new Callable<AVObject>() {
            @Override
            public AVObject call() throws Exception {
                BnRemote bnRemote = new BnRemote();
                bnRemote.put(BnItem.getUserName(), AVObject.createWithoutData(AVUser.class,bnItem.getUser().getObjectId()));
                bnRemote.put(BnItem.getContentFieldName(),bnItem.getContent());
                bnRemote.put(BnItem.getHasFavortFieldName(),bnItem.hasFavort());
                bnRemote.put(BnItem.getHasCommentFieldName(),bnItem.hasComment());
                bnRemote.put(BnItem.getCreateTimeFieldName(),bnItem.getCreateTime());
                bnRemote.put(BnItem.getPhotosFieldName(),toRemoteHashMap(bnItem.getPhotos()));
                bnRemote.put(BnItem.getTypeFieldName(),bnItem.getType());
                bnRemote.put(BnItem.getLinkImgFieldName(),bnItem.getLinkImg());
                bnRemote.put(BnItem.getLinkTitleFieldName(),bnItem.getLinkTitle());
                bnRemote.put(BnItem.getVideoImgUrlFieldName(),bnItem.getVideoImgUrl());
                bnRemote.put(BnItem.getVideoUrlFieldName(),bnItem.getVideoUrl());
                bnRemote.put(BnItem.getIsAllFieldName(),bnItem.isAll());
                return bnRemote;
            }
        });

    }

    public static HashMap<Object,Object> toRemoteHashMap(HashMap<String, String> photos) {
        if (photos == null){
            return null;
        }
        HashMap remotePhotos = new HashMap();
        try {
            for (Map.Entry<String,String> entry : photos.entrySet()){
                remotePhotos.put(DesConstant.BN_PHOTOS_DESUTIL.encrypt(entry.getKey()),
                        DesConstant.BN_PHOTOS_DESUTIL.encrypt(entry.getValue()));
            }
        }catch (Exception e){
        }
        return remotePhotos;
    }
}

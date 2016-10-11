package com.vivifram.second.hitalk.bean.parser;

import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.vivifram.second.hitalk.bean.blackboard.BnFavortRemote;
import com.vivifram.second.hitalk.bean.blackboard.BnItem;
import com.vivifram.second.hitalk.bean.blackboard.BnRemote;
import com.vivifram.second.hitalk.bean.blackboard.FavortItem;

import java.util.concurrent.Callable;

import bolts.Task;

/**
 * Created by zuowei on 16-8-25.
 */
public class FavortParser extends BeanParser<FavortItem> {
    @Override
    public Task<AVObject> encoder(final FavortItem b) {

        return Task.callInBackground(new Callable<AVObject>() {
            @Override
            public AVObject call() throws Exception {
                BnFavortRemote bnFavortRemote = new BnFavortRemote();
                bnFavortRemote.put(BnItem.asBnTargetName(),AVObject.createWithoutData(BnRemote.class,b.getBnRemoteId()));
                bnFavortRemote.put(FavortItem.getUserFieldName(),AVObject.createWithoutData(AVUser.class,b.getUser().getObjectId()));
                bnFavortRemote.put(FavortItem.getCreateTimeFieldName(),b.getCreateTime());
                return bnFavortRemote;
            }
        });
    }
}

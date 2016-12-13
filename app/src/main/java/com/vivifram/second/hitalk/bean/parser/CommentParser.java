package com.vivifram.second.hitalk.bean.parser;

import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.vivifram.second.hitalk.bean.blackboard.BnCommentRemote;
import com.vivifram.second.hitalk.bean.blackboard.BnItem;
import com.vivifram.second.hitalk.bean.blackboard.BnRemote;
import com.vivifram.second.hitalk.bean.blackboard.CommentItem;

import java.util.concurrent.Callable;

import bolts.Task;

/**
 * Created by zuowei on 16-8-24.
 */
public class CommentParser extends BeanParser<CommentItem> {

    @Override
    public Task<AVObject> encoder(final CommentItem b) {

        return Task.callInBackground(new Callable<AVObject>() {
            @Override
            public AVObject call() throws Exception {
                BnCommentRemote bnCommentRemote = new BnCommentRemote();
                bnCommentRemote.put(BnItem.asBnTargetName(),AVObject.createWithoutData(BnRemote.class,b.getBnRemoteId()));
                bnCommentRemote.put(CommentItem.getContentFieldName(),b.getContent());
                bnCommentRemote.put(CommentItem.getUserFieldName(),AVObject.createWithoutData(AVUser.class,b.getUser().getObjectId()));
                if (b.getToReplyUser() != null) {
                    bnCommentRemote.put(CommentItem.getToReplyUserFieldName(), AVObject.createWithoutData(AVUser.class, b.getToReplyUser().getObjectId()));
                }
                bnCommentRemote.put(CommentItem.getCreateTimeFieldName(),b.getCreateTime());
                return bnCommentRemote;
            }
        });
    }
}

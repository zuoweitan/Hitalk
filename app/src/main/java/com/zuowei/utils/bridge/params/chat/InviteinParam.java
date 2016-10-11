package com.zuowei.utils.bridge.params.chat;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;

/**
 * Created by zuowei on 16-8-5.
 */
public class InviteinParam extends ConversationParam {
    public AVIMClient client_;
    public AVIMConversation avimConversation_;
    public String invitedBy;
    @Override
    public int getActionType() {
        return ACTION_INVITE;
    }

    public InviteinParam(AVIMClient client_, AVIMConversation avimConversation_, String invitedBy) {
        this.client_ = client_;
        this.avimConversation_ = avimConversation_;
        this.invitedBy = invitedBy;
    }
}

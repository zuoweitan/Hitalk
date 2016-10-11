package com.zuowei.utils.bridge.params.chat;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;

import java.util.List;

/**
 * Created by zuowei on 16-8-5.
 */
public class MemberLeftParam extends ConversationParam {
    public AVIMClient client_;
    public AVIMConversation avimConversation_;
    public List<String> members_;
    public String kickedBy_;
    @Override
    public int getActionType() {
        return ACTION_MEMBER_LEFT;
    }

    public MemberLeftParam(AVIMClient client_, AVIMConversation avimConversation_, List<String> members_, String kickedBy_) {
        this.client_ = client_;
        this.avimConversation_ = avimConversation_;
        this.members_ = members_;
        this.kickedBy_ = kickedBy_;
    }
}

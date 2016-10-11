package com.zuowei.utils.bridge.params.chat;

import com.zuowei.utils.bridge.constant.EaterAction;
import com.zuowei.utils.bridge.params.LightParam;

/**
 * Created by zuowei on 16-8-5.
 */
public abstract class ConversationParam extends LightParam{
    public static final int ACTION_OFFLINE_MESSAGE_COUNT_CHANGED = 0x01;
    public static final int ACTION_MEMBER_LEFT = 0x02;
    public static final int ACTION_MEMBER_JOIN = 0x03;
    public static final int ACTION_KICK = 0x04;
    public static final int ACTION_INVITE = 0x05;
    public ConversationParam() {
        super(EaterAction.ACTION_DO_CHECK_CONVERSATION);
    }

    public abstract int getActionType();
}

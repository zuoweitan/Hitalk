package com.zuowei.utils.bridge.params.chat;

/**
 * Created by zuowei on 16-8-5.
 */
public class OfflineMessageCountChangedParam extends ConversationParam {
    @Override
    public int getActionType() {
        return ACTION_OFFLINE_MESSAGE_COUNT_CHANGED;
    }
}

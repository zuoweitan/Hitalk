package com.zuowei.utils.handlers;


import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationEventHandler;
import com.zuowei.utils.bridge.EaterManager;
import com.zuowei.utils.bridge.params.chat.InviteinParam;
import com.zuowei.utils.bridge.params.chat.KickoutParam;
import com.zuowei.utils.bridge.params.chat.MemberJoinParam;
import com.zuowei.utils.bridge.params.chat.MemberLeftParam;
import com.zuowei.utils.bridge.params.chat.OfflineMessageCountChangedParam;
import com.zuowei.utils.common.NLog;
import com.zuowei.utils.common.TagUtil;
import com.zuowei.utils.helper.ConversationCacheHelper;

import java.util.List;

/**
 * Created by zuowei on 16-8-4.
 */
public class ConversationHandler extends AVIMConversationEventHandler {

    private static ConversationHandler sInstance;
    private ConversationHandler(){}

    public static ConversationHandler getInstance(){
        if (sInstance == null) {
            synchronized (ConversationHandler.class){
                if (sInstance == null) {
                    sInstance = new ConversationHandler();
                }
            }
        }
        return sInstance;
    }

    public void onOfflineMessagesUnread(AVIMClient client, AVIMConversation conversation, int unreadCount) {
        if(unreadCount > 0) {
            ConversationCacheHelper.getInstance().increaseUnreadCount(conversation.getConversationId(), unreadCount);
            EaterManager.getInstance().broadCastSticky(new OfflineMessageCountChangedParam());
        }

    }

    public void onMemberLeft(AVIMClient client, AVIMConversation conversation, List<String> members, String kickedBy) {
        NLog.i(TagUtil.makeTag(ConversationHandler.class),"onMemberLeft");
        EaterManager.getInstance().broadCastSticky(new MemberLeftParam(client,conversation,members,kickedBy));
    }

    public void onMemberJoined(AVIMClient client, AVIMConversation conversation, List<String> members, String invitedBy) {
        NLog.i(TagUtil.makeTag(ConversationHandler.class),"onMemberJoined");
        EaterManager.getInstance().broadCastSticky(new MemberJoinParam(client,conversation,members,invitedBy));
    }

    public void onKicked(AVIMClient client, AVIMConversation conversation, String kickedBy) {
        NLog.i(TagUtil.makeTag(ConversationHandler.class),"onKicked");
        EaterManager.getInstance().broadCastSticky(new KickoutParam(client,conversation,kickedBy));
    }

    public void onInvited(AVIMClient client, AVIMConversation conversation, String operator) {
        NLog.i(TagUtil.makeTag(ConversationHandler.class),"onInvited");
        EaterManager.getInstance().broadCastSticky(new InviteinParam(client,conversation,operator));
    }
}

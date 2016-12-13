package com.zuowei.utils.handlers;

import android.content.Context;
import android.content.Intent;

import com.avos.avoscloud.AVCallback;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.AVIMTypedMessageHandler;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.vivifram.second.hitalk.HiTalkApplication;
import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.bean.ChatConstants;
import com.zuowei.dao.greendao.User;
import com.zuowei.utils.bridge.EaterManager;
import com.zuowei.utils.bridge.params.chat.MessageParam;
import com.zuowei.utils.common.NLog;
import com.zuowei.utils.common.NotificationUtils;
import com.zuowei.utils.common.TagUtil;
import com.zuowei.utils.helper.ConversationCacheHelper;
import com.zuowei.utils.helper.HiTalkHelper;
import com.zuowei.utils.helper.UserBeanCacheHelper;

/**
 * Created by zuowei on 16-8-4.
 */
public class MessageHandler extends AVIMTypedMessageHandler<AVIMTypedMessage> {

    private static MessageHandler sInstance;
    private Context mCtx;
    private MessageHandler(){
        mCtx = HiTalkApplication.mAppContext;
    }
    public static MessageHandler getInstance(){
        if (sInstance == null){
            synchronized (MessageHandler.class){
                if (sInstance == null) {
                    sInstance = new MessageHandler();
                }
            }
        }
        return sInstance;
    }

    @Override
    public void onMessage(AVIMTypedMessage message, AVIMConversation conversation, AVIMClient client) {
        super.onMessage(message, conversation, client);
        NLog.i(TagUtil.makeTag(MessageHandler.class),"message = "+message);
        if (message != null && message.getMessageId() != null){
            client.close(null);
        }else if (!client.getClientId().equals(HiTalkHelper.getInstance().getCurrentUserId())){
            client.close(null);
        }else if (!message.getFrom().equals(client.getClientId())){
            if (NotificationUtils.isShowNotification(conversation.getConversationId())){
                sendNotification(message,conversation);
            }
            ConversationCacheHelper.getInstance().increaseUnreadCount(message.getConversationId());
            sendEvent(message,conversation);
        }else {
            ConversationCacheHelper.getInstance().insertConversation(message.getConversationId());
        }
    }

    private void sendEvent(AVIMTypedMessage message, AVIMConversation conversation) {
        MessageParam messageParam = new MessageParam();
        messageParam.mMessageAction = MessageParam.MESSAGE_ACTION_RECEIVED;
        messageParam.message = message;
        messageParam.conversation = conversation;
        EaterManager.getInstance().broadcast(messageParam);
    }

    @Override
    public void onMessageReceipt(AVIMTypedMessage message, AVIMConversation conversation, AVIMClient client) {
        super.onMessageReceipt(message, conversation, client);
    }

    private void sendNotification(final AVIMTypedMessage message, final AVIMConversation conversation) {
        if(null != conversation && null != message) {
            final String notificationContent = message instanceof AVIMTextMessage ?((AVIMTextMessage)message).getText():
                    this.mCtx.getString(R.string.unspport_message_type);
            UserBeanCacheHelper.getInstance().getCachedUser(message.getFrom(), new AVCallback<User>() {
                @Override
                protected void internalDone0(User user, AVException e) {
                    if (user != null){
                        String title = user.getNick();
                        Intent intent = getIMNotificationIntent(conversation.getConversationId(), message.getFrom());
                        NotificationUtils.showNotification(mCtx, title, notificationContent,null, intent);
                    }
                }
            });
        }

    }

    private Intent getIMNotificationIntent(String conversationId, String peerId) {
        Intent intent = new Intent();
        intent.setAction(ChatConstants.CHAT_NOTIFICATION_ACTION);
        intent.putExtra(ChatConstants.CONVERSATION_ID, conversationId);
        intent.putExtra(ChatConstants.PEER_ID, peerId);
        intent.setPackage(mCtx.getPackageName());
        intent.addCategory("android.intent.category.DEFAULT");
        return intent;
    }
}

package com.vivifram.second.hitalk.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

import com.avos.avoscloud.AVCallback;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.callback.AVIMMessagesQueryCallback;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.base.BaseActivity;
import com.vivifram.second.hitalk.base.EatMark;
import com.vivifram.second.hitalk.base.InterfaceInject;
import com.vivifram.second.hitalk.base.LayoutInject;
import com.vivifram.second.hitalk.bean.Constants;
import com.vivifram.second.hitalk.bean.Emojicon;
import com.vivifram.second.hitalk.bean.IMessageWrap;
import com.vivifram.second.hitalk.manager.chat.ClientManager;
import com.vivifram.second.hitalk.ui.layout.ChatRoomLayout;
import com.vivifram.second.hitalk.ui.page.layout.ChatInputMenuLayout;
import com.vivifram.second.hitalk.ui.page.layout.ChatMessageListLayout;
import com.zuowei.dao.greendao.User;
import com.zuowei.utils.bridge.EaterManager;
import com.zuowei.utils.bridge.constant.EaterAction;
import com.zuowei.utils.bridge.params.LightParam;
import com.zuowei.utils.bridge.params.chat.MessageParam;
import com.zuowei.utils.common.NLog;
import com.zuowei.utils.common.NToast;
import com.zuowei.utils.common.SyncUtils;
import com.zuowei.utils.common.TagUtil;
import com.zuowei.utils.handlers.AbstractHandler;
import com.zuowei.utils.helper.UserBeanCacheHelper;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

import bolts.Task;

/**
 * 项目名称：Hitalk
 * 类描述：
 * 创建人：zuowei
 * 创建时间：17-2-20 上午10:49
 * 修改人：zuowei
 * 修改时间：17-2-20 上午10:49
 * 修改备注：
 */

@LayoutInject(name = "ChatRoomLayout")
    public class ChatRoomActivity extends BaseActivity<ChatRoomLayout>{
    private static final String TAG = TagUtil.makeTag(ChatRoomActivity.class);

    public static void start(Context c,int key){
        start(c,ChatRoomActivity.class,key);
    }

    private AVIMConversation conversation;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_chat_room_layout);
        SparseArray chatParams = (SparseArray) params;
        switch (chatParams.keyAt(0)){
            case Constants.ParamsKey.Chat.TO_FRIEND:
                    String toUserId = (String) chatParams.get(Constants.ParamsKey.Chat.TO_FRIEND);
                    UserBeanCacheHelper.getInstance().getCachedUser(toUserId, new AVCallback<User>() {
                        @Override
                        protected void internalDone0(User user, AVException e) {
                            if (e == null) {
                                doInitChatRoom(user);
                            } else {
                                NToast.shortToast(mAppCtx,getResources().getString(R.string.open_chat_room_failed));
                                finish();
                            }
                        }
                    });
                break;
        }
    }


    private void updateConversation(AVIMConversation conversation){
        this.conversation = conversation;
        fetchMessages().continueWith(task -> {
            if (task.getResult() == null){
                NToast.shortToast(mAppCtx, getString(R.string.message_fetch_failed_warn));
            } else {
                mLayout.setMessageListFreshListener(new ChatMessageListLayout.IMessageListFreshListener() {
                    @Override
                    public void onRefresh() {
                        if (mLayout != null){
                            IMessageWrap firstMessage = mLayout.getChatLt().getFirstMessage();
                            if (firstMessage == null){
                                fetchMessages().continueWith(task -> {
                                    mLayout.getChatLt().finshRefresh();
                                    return null;
                                });
                            }else {
                                if (conversation != null) {
                                    conversation.queryMessages(firstMessage.message_.getMessageId(),
                                            firstMessage.message_.getTimestamp(), 20, new AVIMMessagesQueryCallback() {
                                                @Override
                                                public void done(List<AVIMMessage> list, AVIMException e) {
                                                    if (e == null && list != null){
                                                        mLayout.pushMessagesAndRefreshToTop(IMessageWrap.buildFrom(list,true));
                                                    }
                                                    mLayout.getChatLt().finshRefresh();
                                                }
                                            });
                                }else {
                                    mLayout.getChatLt().finshRefresh();
                                }

                            }
                        }
                    }

                    @Override
                    public void onLoadmore() {

                    }
                });
            }
            return null;
        });
    }

    private Task<List<AVIMMessage>> fetchMessages() {
        if (conversation != null) {
            return Task.callInBackground(new Callable<List<AVIMMessage>>() {
                List<AVIMMessage> queryResult;
                @Override
                public List<AVIMMessage> call() throws Exception {
                    int syncLatch = SyncUtils.getSyncLatch();
                    conversation.queryMessages(new AVIMMessagesQueryCallback() {
                        @Override
                        public void done(List<AVIMMessage> list, AVIMException e) {
                            if (filterException(e)) {
                                queryResult = list;
                            }
                            SyncUtils.notify(syncLatch);
                        }
                    });
                    SyncUtils.wait(syncLatch);
                    NLog.i(TagUtil.makeTag(getClass()),"fetchMessages queryResult = "+queryResult);
                    return queryResult;
                }
            }).continueWith(task -> {
                List<AVIMMessage> result = task.getResult();
                if (result != null && mLayout.getChatLt() != null){
                    mLayout.getChatLt().clear();
                    mLayout.pushMessagesAndRefreshToBottom(IMessageWrap.buildFrom(result,false));
                    return result;
                }
                return null;
            },Task.UI_THREAD_EXECUTOR);
        }
        return Task.forResult(null);
    }

    private boolean filterException(Exception e) {
        if(null != e) {
            NToast.shortToast(mAppCtx,e.getMessage());
        }

        return null == e;
    }

    private void doInitChatRoom(User user) {
        mLayout.setTitle(user.getNick());
        if (ClientManager.getInstance().isOpend()){
            ClientManager.getInstance().getClient().createConversation(Collections.singletonList(user.getObjectId()),
                    user.getNick(), null, false, true, new AVIMConversationCreatedCallback() {
                        @Override
                        public void done(AVIMConversation avimConversation, AVIMException e) {
                               if (e != null){
                                   NToast.shortToast(mAppCtx,getResources().getString(R.string.create_chat_room_failed));
                                   NLog.e(TAG, "conversation create failed");
                                   finish();
                               } else {
                                   updateConversation(avimConversation);
                               }
                        }
                    });
        } else {
            NLog.e(TAG,"client is not open");
            NToast.shortToast(mAppCtx,getResources().getString(R.string.open_chat_room_failed));
            finish();
        }
    }

    @InterfaceInject(bindName = "chatInputMenuListener")
    ChatInputMenuLayout.ChatInputMenuListener chatInputMenuListener = new ChatInputMenuLayout.ChatInputMenuListener() {
        @Override
        public void onSendMessage(String content) {
            NLog.i(TAG, "onSendMessage content = "+content);
            EaterManager.getInstance().broadcast(MessageParam.obtainTextMessage(content, conversation));
        }

        @Override
        public void onBigExpressionClicked(Emojicon emojicon) {

        }

        @Override
        public boolean onPressToSpeakBtnTouch(View v, MotionEvent event) {
            return false;
        }
    };

    @InterfaceInject(bindName = "layoutActionListener")
    ChatRoomLayout.OnLayoutActionListener layoutActionListener = this::finish;

    @EatMark(action = EaterAction.ACTION_DO_CHECK_MESSAGE)
    class MessageReceiver extends AbstractHandler<MessageParam> {

        @Override
        public boolean isParamAvailable(LightParam param) {
            return param != null && param instanceof MessageParam;
        }

        @Override
        public void doJobWithParam(MessageParam param) {
            switch (param.mMessageAction){
                case MessageParam.MESSAGE_ACTION_RECEIVED:
                    onMessageReceived(param.conversation, param.message);
                    break;
                case MessageParam.MESSAGE_ACTION_SEND_TEXT:
                    NLog.i(TAG, "MESSAGE_ACTION_SEND_TEXT param.messageText = "+param.messageText);
                    if (isValidMessage(param)) {
                        sendTextMessage(param.messageText);
                    }
                    break;
                case MessageParam.MESSAGE_ACTION_SEND_IMAGE:
                    break;
                case MessageParam.MESSAGE_ACTION_SEND_LOCATION:
                    break;
                case MessageParam.MESSAGE_ACTION_SEND_VOICE:
                    break;
                case MessageParam.MESSAGE_ACTION_SEND_VIDEO:
                    break;
                case MessageParam.MESSAGE_ACTION_SEND_FILE:
                    break;
            }
        }
    }

    private boolean isValidMessage(MessageParam param) {
        return Objects.equals(param.conversation.getConversationId(), conversation.getConversationId());
    }

    private void sendTextMessage(String messageText) {
        AVIMTextMessage message = new AVIMTextMessage();
        message.setText(messageText);
        sendMessage(message);
    }

    private void onMessageReceived(AVIMConversation conversation, AVIMTypedMessage message) {
        if(conversation != null && conversation != null &&
                conversation.getConversationId().equals(conversation.getConversationId())) {
            switch (message.getMessageType()){
                case IMessageWrap.MESSAGE_TYPE_TEXT:
                    mLayout.pushMessagesAndRefreshToBottom(IMessageWrap.buildFrom(Collections.singletonList(message), false));
                    break;
            }
        }
    }


    public void sendMessage(AVIMTypedMessage message) {
        this.sendMessage(message, true);
    }

    public void sendMessage(final AVIMTypedMessage message, boolean addToList) {
        message.setMessageIOType(AVIMMessage.AVIMMessageIOType.AVIMMessageIOTypeOut);
        if(addToList) {
            mLayout.pushMessages(IMessageWrap.buildFrom(message,false));
        }
        mLayout.getChatLt().refreshSelectLast();
        mLayout.getChatLt().postToList(() -> {
            if (conversation != null) {
                conversation.sendMessage(message, new AVIMConversationCallback() {
                    public void done(AVIMException e) {
                        IMessageWrap iMessageWrap = mLayout.getChatLt().findMessageWrap(message);
                        if (iMessageWrap != null &&
                                iMessageWrap.onIMessageStateChangedListener_ != null){
                            iMessageWrap.onIMessageStateChangedListener_.onIMessageStateChanged(message.getMessageStatus());
                        }
                    }
                });
            }
        });
    }
}

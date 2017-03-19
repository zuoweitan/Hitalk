package com.vivifram.second.hitalk.ui.page;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationMemberCountCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;
import com.avos.avoscloud.im.v2.callback.AVIMMessagesQueryCallback;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.vivifram.second.hitalk.HitalkMonitor;
import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.base.InterfaceInject;
import com.vivifram.second.hitalk.base.LayoutInject;
import com.vivifram.second.hitalk.bean.Emojicon;
import com.vivifram.second.hitalk.bean.IMessageWrap;
import com.vivifram.second.hitalk.broadcast.ConnectivityNotifier;
import com.vivifram.second.hitalk.manager.chat.ClientManager;
import com.vivifram.second.hitalk.manager.chat.ConversationClient;
import com.vivifram.second.hitalk.ui.page.layout.ChatInputMenuLayout;
import com.vivifram.second.hitalk.ui.page.layout.ChatMessageListLayout;
import com.vivifram.second.hitalk.ui.page.layout.HitalkFragmentSub1Layout;
import com.zuowei.utils.bridge.EaterManager;
import com.zuowei.utils.bridge.constant.EaterAction;
import com.zuowei.utils.bridge.params.LightParam;
import com.zuowei.utils.bridge.params.chat.ClientOpenParam;
import com.zuowei.utils.bridge.params.chat.ConversationParam;
import com.zuowei.utils.bridge.params.chat.MessageParam;
import com.zuowei.utils.common.NLog;
import com.zuowei.utils.common.NToast;
import com.zuowei.utils.common.NotificationUtils;
import com.zuowei.utils.common.SyncUtils;
import com.zuowei.utils.common.TagUtil;
import com.zuowei.utils.handlers.AbstractHandler;
import com.zuowei.utils.handlers.ClientOpenHandler;
import com.zuowei.utils.helper.ConversationCacheHelper;
import com.zuowei.utils.helper.HiTalkHelper;

import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;

import bolts.Task;

/**
 * Created by zuowei on 16-7-25.
 */
@LayoutInject(name = "HitalkFragmentSub1Layout")
public class HiTalkFragmentSub1 extends LazyFragment<HitalkFragmentSub1Layout> {
    private AVIMConversation mAvimConversation;
    private MessageReceiver mMessageReceiver;
    private AbstractHandler<ClientOpenParam> mClientOpenListener;
    private HiTalkFragment mParent;
    private AbstractHandler<ConversationParam> mConversationHandler;
    private Queue<Integer> mRequest;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        NLog.i(TagUtil.makeTag(HiTalkFragmentSub1.class),"onAttach");
        mRequest = new ConcurrentLinkedQueue<>();
        createAndInitConversation();
        new HitalkMonitor.MonitorEvent(){

            @Override
            public boolean check() {
                return mAvimConversation != null;
            }

            @Override
            public void run() {
                if (!ClientManager.getInstance().isOpend()) {
                    if (ConnectivityNotifier.isConnected(mAppCtx)) {
                        ClientManager.getInstance().open(HiTalkHelper.getInstance().getCurrentUserId(),null);
                    } else {
                        //NToast.shortToast(mAppCtx, R.string.internet_not_connect_warn);
                    }

                }
                if (ClientManager.getInstance().isOpend()) {
                    initSquareConversation();
                }
            }

            @Override
            public int getMsgType() {
                return HitalkMonitor.CHECK_HITALK_CONVERSATION;
            }
        }.sendToMonitor();
    }

    private void createAndInitConversation() {
        if (ClientManager.getInstance().isOpend()) {
            initSquareConversation();
        }
        listenToClient();
        listenToConversation();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void listenToConversation() {
        mConversationHandler = new AbstractHandler<ConversationParam>() {
            @Override
            public boolean isParamAvailable(LightParam param) {
                return param != null && param instanceof ConversationParam;
            }

            @Override
            public void doJobWithParam(ConversationParam param) {
                switch (param.getActionType()){
                    case ConversationParam.ACTION_MEMBER_JOIN:
                    case ConversationParam.ACTION_MEMBER_LEFT:
                            if (mAvimConversation != null){
                                mAvimConversation.getMemberCount(new AVIMConversationMemberCountCallback() {
                                    @Override
                                    public void done(Integer integer, AVIMException e) {
                                        if (e == null && integer != null){
                                            EaterManager.getInstance().broadCastSticky(
                                                    new LightParam.Builder("square_count").setArg1(integer).create());
                                        }
                                    }
                                });
                            }
                        break;
                }
            }
        };
    }

    private void listenToClient() {
        mClientOpenListener = new ClientOpenHandler() {
            @Override
            public void doJobWithParam(ClientOpenParam param) {
                NLog.i(TagUtil.makeTag(getClass()),"mClientOpenListener callback and param.mOpened = "+param.mOpened);
                if (param.mOpened){
                    initSquareConversation();
                }else {
                    NToast.shortToast(mAppCtx,getString(R.string.internet_not_connect_warn));
                }
            }
        };
    }

    private void initSquareConversation() {
        NLog.i(TagUtil.makeTag(getClass()),"initSquareConversation");
        String currentSquareConversationName = HiTalkHelper.getInstance().getModel().getSquareConversationName();
        if (HiTalkHelper.getInstance().getCurrentUserCollege() != null &&
                ConversationClient.getCollegeSquareConversationName(HiTalkHelper.getInstance().getCurrentUserCollege())
                        .equals(currentSquareConversationName)){
            mAvimConversation = ClientManager.getInstance().getClient().getConversation(
                    HiTalkHelper.getInstance().getModel().getSquareConversationId());

            updateConversation(mAvimConversation);
            queryInSquare();
        }else {
            ConversationClient.getInstance().getSquareConversationByName(
                    ConversationClient.getCollegeSquareConversationName(HiTalkHelper.getInstance().getCurrentUserCollege()), new AVIMConversationQueryCallback() {
                        @Override
                        public void done(List<AVIMConversation> list, AVIMException e) {
                            if (e == null && list.size() > 0){
                                final AVIMConversation avimConversation = list.get(0);
                                ConversationCacheHelper.getInstance().insertConversation(avimConversation.getConversationId());
                                HiTalkHelper.getInstance().getModel().setSquareConversationId(avimConversation.getConversationId());
                                HiTalkHelper.getInstance().getModel().setSquareConversationName(avimConversation.getName());
                                mAvimConversation = avimConversation;
                                updateConversation(mAvimConversation);
                                queryInSquare();
                            }else {
                               NToast.shortToast(mAppCtx,getString(R.string.square_not_create));
                            }
                        }
                    });
        }
    }

    private void queryInSquare() {
        if (mAvimConversation != null) {
            ConversationClient.queryInSquare(mAvimConversation.getConversationId(), new AVIMConversationQueryCallback() {
                @Override
                public void done(List<AVIMConversation> list, AVIMException e) {
                    NLog.i(TagUtil.makeTag(HiTalkFragmentSub1.class),"list = "+list);
                    if (e != null || list.size() <= 0){
                        mAvimConversation.join(new AVIMConversationCallback() {
                            @Override
                            public void done(AVIMException e) {
                                NLog.i(TagUtil.makeTag(HiTalkFragmentSub1.class),"message : " + (e == null ? "null":e.getMessage()));
                                if (e != null){
                                    NToast.shortToast(mAppCtx,mAppCtx.getString(R.string.join_square_failed));
                                }
                                mAvimConversation.getMemberCount(new AVIMConversationMemberCountCallback() {
                                    @Override
                                    public void done(Integer integer, AVIMException e) {
                                        if (e == null && integer != null){
                                           EaterManager.getInstance().broadCastSticky(
                                                   new LightParam.Builder("square_count").setArg1(integer).create());
                                        }else {
                                            EaterManager.getInstance().broadCastSticky(
                                                    new LightParam.Builder("square_count").setArg1(0).create());
                                        }
                                    }
                                });
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void onViewCreated() {
        super.onViewCreated();
        init();
        mLayout.setMessageListFreshListener(new ChatMessageListLayout.IMessageListFreshListener() {
            @Override
            public void onRefresh() {
                NLog.i(TagUtil.makeTag(getClass()),"mAvimConversation = "+mAvimConversation);
                if (mAvimConversation == null || !isInternetConnected){
                    if (!isInternetConnected){
                        NToast.shortToast(mAppCtx, R.string.internet_not_connect_warn);
                    } else {
                        return;
                    }
                }
                if (mLayout != null){
                    IMessageWrap firstMessage = mLayout.getChatLt().getFirstMessage();
                    if (firstMessage == null){
                        fetchMessages().continueWith(task -> {
                            mLayout.getChatLt().finshRefresh();
                            return null;
                        });
                    }else {
                        if (mAvimConversation != null) {
                            mAvimConversation.queryMessages(firstMessage.message_.getMessageId(),
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

        if (mRequest.size() > 0){
            mRequest.poll();
            fetchMessages();
        }
    }

    private void init() {
        mMessageReceiver = new MessageReceiver();
        EaterManager.getInstance().registerEater(EaterAction.ACTION_DO_CHECK_MESSAGE,mMessageReceiver);
        EaterManager.getInstance().registerEater(EaterAction.ACTION_DO_CHECK_CLIENT,mClientOpenListener);
        EaterManager.getInstance().registerEater(EaterAction.ACTION_DO_CHECK_CONVERSATION,mConversationHandler);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EaterManager.getInstance().unRegisterEater(mMessageReceiver);
        EaterManager.getInstance().unRegisterEater(mClientOpenListener);
        EaterManager.getInstance().unRegisterEater(mConversationHandler);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAvimConversation != null) {
            NotificationUtils.addTag(mAvimConversation.getConversationId());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAvimConversation != null) {
            NotificationUtils.removeTag(mAvimConversation.getConversationId());
        }
    }

    @Override
    protected void lazyLoad() {

    }

    public void updateConversation(AVIMConversation conversation){
        NLog.i(TagUtil.makeTag(HiTalkFragmentSub1.class),"conversation = "+conversation);
        mAvimConversation = conversation;
        if (mAvimConversation != null) {
            NotificationUtils.addTag(mAvimConversation.getConversationId());
        }
        if (mLayout != null) {
            fetchMessages();
        }else {
            mRequest.add(1);
        }
    }

    private Task<List<AVIMMessage>> fetchMessages() {
        if (mAvimConversation != null) {
            return Task.callInBackground(new Callable<List<AVIMMessage>>() {
                List<AVIMMessage> queryResult;
                @Override
                public List<AVIMMessage> call() throws Exception {
                    int syncLatch = SyncUtils.getSyncLatch();
                    mAvimConversation.queryMessages(new AVIMMessagesQueryCallback() {
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

    @InterfaceInject(bindName = "chatInputMenuListener")
    ChatInputMenuLayout.ChatInputMenuListener chatInputMenuListener = new ChatInputMenuLayout.ChatInputMenuListener() {
        @Override
        public void onSendMessage(String content) {
            EaterManager.getInstance().broadcast(MessageParam.obtainTextMessage(content, mAvimConversation));
        }

        @Override
        public void onBigExpressionClicked(Emojicon emojicon) {

        }

        @Override
        public boolean onPressToSpeakBtnTouch(View v, MotionEvent event) {
            return false;
        }
    };

    class MessageReceiver extends AbstractHandler<MessageParam> {

        @Override
        public boolean isParamAvailable(LightParam param) {
            return param != null && param instanceof MessageParam;
        }

        @Override
        public void doJobWithParam(MessageParam param) {
            switch (param.mMessageAction){
                case MessageParam.MESSAGE_ACTION_RECEIVED:
                    onMessageReceived(param.conversation,param.message);
                    break;
                case MessageParam.MESSAGE_ACTION_SEND_TEXT:
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
        return param.conversation != null && param.conversation.getConversationId() == mAvimConversation.getConversationId();
    }

    private void sendTextMessage(String messageText) {
        AVIMTextMessage message = new AVIMTextMessage();
        message.setText(messageText);
        sendMessage(message);
    }

    private void onMessageReceived(AVIMConversation conversation, AVIMTypedMessage message) {
        if(mAvimConversation != null && conversation != null &&
                mAvimConversation.getConversationId().equals(conversation.getConversationId())) {
            switch (message.getMessageType()){
                case IMessageWrap.MESSAGE_TYPE_TEXT:
                    mLayout.pushMessagesAndRefreshToBottom(IMessageWrap.buildFrom(Collections.<AVIMMessage>singletonList(message), false));
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
        mLayout.getChatLt().postToList(new Runnable() {
            @Override
            public void run() {
                if (mAvimConversation != null) {
                    mAvimConversation.sendMessage(message, new AVIMConversationCallback() {
                        public void done(AVIMException e) {
                            IMessageWrap iMessageWrap = mLayout.getChatLt().findMessageWrap(message);
                            if (iMessageWrap != null &&
                                    iMessageWrap.onIMessageStateChangedListener_ != null){
                                iMessageWrap.onIMessageStateChangedListener_.onIMessageStateChanged(message.getMessageStatus());
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_hi_sub_1_layout;
    }

    public void setParent(HiTalkFragment mParent) {
        this.mParent = mParent;
    }
}

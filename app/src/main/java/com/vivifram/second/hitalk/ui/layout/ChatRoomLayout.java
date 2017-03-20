package com.vivifram.second.hitalk.ui.layout;

import android.view.View;
import android.widget.Button;

import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.base.BaseLayoutBean;
import com.vivifram.second.hitalk.base.BindView;
import com.vivifram.second.hitalk.bean.IMessageWrap;
import com.vivifram.second.hitalk.cache.MessageCacheQueue;
import com.vivifram.second.hitalk.ui.page.layout.ChatExtendMenuLayout;
import com.vivifram.second.hitalk.ui.page.layout.ChatInputMenuLayout;
import com.vivifram.second.hitalk.ui.page.layout.ChatMessageListLayout;
import com.vivifram.second.hitalk.ui.view.BGATitlebar;

import java.util.List;

/**
 * 项目名称：Hitalk
 * 类描述：
 * 创建人：zuowei
 * 创建时间：17-2-20 上午10:51
 * 修改人：zuowei
 * 修改时间：17-2-20 上午10:51
 * 修改备注：
 */
public class ChatRoomLayout extends BaseLayout{
    static final int ITEM_TAKE_PICTURE = 1;
    static final int ITEM_PICTURE = 2;
    static final int ITEM_LOCATION = 3;
    protected int[] itemStrings = { R.string.attach_take_pic, R.string.attach_picture, R.string.attach_location };
    protected int[] itemdrawables = { R.drawable.chat_takepic_selector, R.drawable.chat_image_selector,
            R.drawable.chat_location_selector };
    protected int[] itemIds = { ITEM_TAKE_PICTURE, ITEM_PICTURE, ITEM_LOCATION };
    public ChatRoomLayout(View rootView) {
        super(rootView);
    }

    @BindView(id = R.id.titleBar)
    private BGATitlebar titlebar;

    private FlowBar flowBar;

    private ChatMessageListLayout chatLt;
    private MenuItemClickListener menuItemClickListener;

    @BindView(id = R.id.chatInput)
    private ChatInputMenuLayout chatMeunLt;

    private ChatInputMenuLayout.ChatInputMenuListener chatInputMenuListener;
    private OnLayoutActionListener layoutActionListener;

    private MessageCacheQueue cache;

    public interface OnLayoutActionListener{
        void onBack();
    }

    @Override
    public void onContentViewCreate(View view) {
        super.onContentViewCreate(view);
        cache = new MessageCacheQueue();
        chatLt = new ChatMessageListLayout(view);
        chatLt.onViewCreate(view);
        chatLt.setMessageFetcher(() -> cache.poll());
        chatLt.setOntouchListener((v, event) -> {
            chatMeunLt.hideKeyboard();
            chatMeunLt.hideExtendMenuContainer();
            return false;
        });
        registerExtendMenuItem();
        menuItemClickListener = new MenuItemClickListener();
        chatMeunLt.init(null);
        chatMeunLt.setChatInputMenuListener(chatInputMenuListener);

        titlebar.setDelegate(new BGATitlebar.BGATitlebarDelegate(){
            @Override
            public void onClickLeftCtv() {
                if (layoutActionListener != null) {
                    layoutActionListener.onBack();
                }
            }
        });

        //delay init
        //flowBar = new FlowBar(findViewById(R.id.flowWarnLt));
    }

    public void setTitle(String title){
        titlebar.setTitleText(title);
    }

    private void registerExtendMenuItem(){
        for(int i = 0; i < itemStrings.length; i++){
            chatMeunLt.registerExtendMenuItem(itemStrings[i], itemdrawables[i], itemIds[i], menuItemClickListener);
        }
    }


    public void pushMessages(List<IMessageWrap<AVIMTypedMessage>> messages){
        if (messages != null) {
            cache.add(messages);
        }
    }

    public void pushMessagesAndRefreshToBottom(List<IMessageWrap<AVIMTypedMessage>> messages){
        pushMessages(messages);
        chatLt.refreshSelectLast();
    }

    public void pushMessagesAndRefreshToTop(List<IMessageWrap<AVIMTypedMessage>> messages){
        pushMessages(messages);
        chatLt.refreshSeekTo(0);
    }

    public void setMessageListFreshListener(ChatMessageListLayout.IMessageListFreshListener mMessageListFreshListener) {
        if (chatLt != null) {
            chatLt.setMessageListFreshListener(mMessageListFreshListener);
        }
    }

    public ChatMessageListLayout getChatLt() {
        return chatLt;
    }

    public FlowBar flowBar() {
        if (flowBar == null) {
            flowBar = new FlowBar(findViewById(R.id.flowWarnLt));
        }
        return flowBar;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        chatLt.onViewDestroy();
    }

    class MenuItemClickListener implements ChatExtendMenuLayout.ChatExtendMenuItemClickListener{

        @Override
        public void onClick(int itemId, View view) {

        }
    }

    public static class FlowBar extends BaseLayoutBean{
        @BindView(id = R.id.addFriendBt, boundClick = true)
        Button addFriendBt;
        @BindView(id = R.id.cancelBt, boundClick = true)
        Button cancelBt;

        private OnFlowBarActionListener onFlowBarActionListener;

        public FlowBar(View base) {
            super(base);
        }

        @Override
        public void onClick(View v) {
            if (onFlowBarActionListener == null) {
                return;
            }
            switch (v.getId()) {
                case R.id.addFriendBt:
                        onFlowBarActionListener.onAddFriend();
                    break;
                case R.id.cancelBt:
                        onFlowBarActionListener.onCancel();
                    break;
            }
        }

        public void show(boolean show) {
            base.setVisibility(show ? View.VISIBLE : View.GONE);
        }

        public FlowBar setOnFlowBarActionListener(OnFlowBarActionListener onFlowBarActionListener) {
            this.onFlowBarActionListener = onFlowBarActionListener;
            return this;
        }

        public interface OnFlowBarActionListener {
            void onAddFriend();
            void onCancel();
        }
    }
}

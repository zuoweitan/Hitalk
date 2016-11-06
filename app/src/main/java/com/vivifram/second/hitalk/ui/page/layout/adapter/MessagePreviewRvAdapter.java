package com.vivifram.second.hitalk.ui.page.layout.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVCallback;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMReservedMessageType;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMMessagesQueryCallback;
import com.avos.avoscloud.im.v2.callback.AVIMSingleMessageQueryCallback;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.bumptech.glide.Glide;
import com.vivifram.second.hitalk.HiTalkApplication;
import com.vivifram.second.hitalk.R;
import com.zuowei.utils.chat.ConversationUtils;
import com.zuowei.utils.common.NLog;
import com.zuowei.utils.common.TagUtil;
import com.zuowei.utils.helper.ConversationCacheHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bingoogolapple.badgeview.BGABadgeRelativeLayout;

/**
 * Created by zuowei on 16-9-25.
 */
public class MessagePreviewRvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_AT = 0x01;
    private static final int TYPE_BOOK = 0x02;
    private static final int TYPE_NORMAL = 0x03;

    private static final int SPECIAL_ITEMS = 2;

    private List<AVIMConversation> conversationList;

    public MessagePreviewRvAdapter(){
        conversationList = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        switch (viewType){
            case TYPE_AT:
                viewHolder = new AtMessageHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.messagelt_at_item,parent,false));
                break;
            case TYPE_BOOK:
                viewHolder = new BookMessageHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.messagelt_book_item,parent,false));
                break;
            default:
                viewHolder = new NormalMessageHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.messagelt_normal_item,parent,false));
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MessageHolder){
            MessageHolder messageHolder = (MessageHolder) holder;
            messageHolder.bindData(getItem(position));
        }
    }

    public void setData(List<AVIMConversation> data){
        NLog.i(TagUtil.makeTag(getClass()),"setData data = "+data);
        conversationList.clear();
        conversationList.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        switch (position){
            case 0:
                return TYPE_AT;
            case 1:
                return TYPE_BOOK;
            default:
                return TYPE_NORMAL;
        }
    }

    public Object getItem(int position){
        switch (getItemViewType(position)){
            case TYPE_NORMAL:
                return conversationList.get(position - SPECIAL_ITEMS);
        }

        return null;
    }

    @Override
    public int getItemCount() {
        return conversationList.size() + SPECIAL_ITEMS;
    }

    public class MessageHolder<T> extends RecyclerView.ViewHolder{
        BGABadgeRelativeLayout bgaBadgeRelativeLayout;
        public MessageHolder(View itemView) {
            super(itemView);
            bgaBadgeRelativeLayout = (BGABadgeRelativeLayout) itemView;
        }

        public void setCount(int count){
            if (count == 0){
                bgaBadgeRelativeLayout.hiddenBadge();
                return;
            }
            bgaBadgeRelativeLayout.showTextBadge(count+"");
        }


        public void bindData(T convId){

        }
    }

    public class NormalMessageHolder extends MessageHolder<AVIMConversation>{
        ImageView avatarView;
        TextView nickTv;
        TextView contentTv;
        TextView timeTv;
        public NormalMessageHolder(View itemView) {
            super(itemView);

            avatarView = (ImageView) itemView.findViewById(R.id.profileIv);
            nickTv = (TextView) itemView.findViewById(R.id.nickTv);
            contentTv = (TextView) itemView.findViewById(R.id.contentTv);
            timeTv = (TextView) itemView.findViewById(R.id.timeTv);

        }

        @Override
        public void bindData(AVIMConversation avimConversation) {
            super.bindData(avimConversation);
            reset();
            if (null != avimConversation) {
                if (null == avimConversation.getCreatedAt()) {
                    avimConversation.fetchInfoInBackground(new AVIMConversationCallback() {
                        public void done(AVIMException e) {
                            if (e != null) {
                                NLog.e(TagUtil.makeTag(getClass()),"bindData fetchInfoInBackground failed :",e);
                            }else {
                                updateName(avimConversation,nickTv);
                                updateAvatar(avimConversation,avatarView);
                            }

                        }
                    });
                } else {
                    updateName(avimConversation,nickTv);
                    updateAvatar(avimConversation,avatarView);
                }
                updateUnreadCount(avimConversation,this);
                updateLastMessageByConversation(avimConversation,contentTv,timeTv);
            }
        }

        private void reset() {
            avatarView.setImageResource(0);
            nickTv.setText("");
            contentTv.setText("");
            timeTv.setText("");
            setCount(0);
        }
    }

    private void updateName(AVIMConversation avimConversation,TextView nickTv){
        ConversationUtils.getConversationName(avimConversation, new AVCallback<String>() {
            protected void internalDone0(String s, AVException e) {
                if(null != e) {
                    NLog.e(TagUtil.makeTag(getClass()),"updateName failed :",e);
                } else {
                    nickTv.setText(s);
                }

            }
        });
    }

    //should recode for groupAvatar
    private void updateAvatar(AVIMConversation conversation,ImageView avatarView) {
        if(null != conversation) {
            if(!conversation.isTransient() && conversation.getMembers().size() <= 2) {
                ConversationUtils.getConversationPeerIcon(conversation, new AVCallback<String>() {
                    protected void internalDone0(String s, AVException e) {
                        if(null != e) {
                            NLog.e(TagUtil.makeTag(getClass()),"updateAvatar failed :",e);
                        }
                        if(!TextUtils.isEmpty(s)) {
                            Glide.with(HiTalkApplication.mAppContext).load(s).placeholder(R.drawable.default_avatar).into(avatarView);
                        } else {
                            avatarView.setImageResource(R.drawable.default_avatar);
                        }

                    }
                });
            } else {
                avatarView.setImageResource(R.drawable.default_group_icon);
            }
        }
    }

    private void updateUnreadCount(AVIMConversation conversation,MessageHolder messageHolder) {
        int num = ConversationCacheHelper.getInstance().getUnreadCount(conversation.getConversationId());
        messageHolder.setCount(num > 0?num:0);
    }

    private void updateLastMessageByConversation(final AVIMConversation conversation,TextView contentTv,TextView timeTv) {
        conversation.getLastMessage(new AVIMSingleMessageQueryCallback() {
            public void done(AVIMMessage avimMessage, AVIMException e) {
                NLog.i(TagUtil.makeTag(getClass()),"updateLastMessageByConversation avimMessage");
                if(null != avimMessage) {
                    updateLastMessage(avimMessage,contentTv,timeTv);
                } else {
                    conversation.queryMessages(1, new AVIMMessagesQueryCallback() {
                        public void done(List<AVIMMessage> list, AVIMException e) {
                            if(null != e) {
                                NLog.e(TagUtil.makeTag(getClass()),"updateLastMessageByConversation failed :",e);
                            }
                            if(null != list && !list.isEmpty()) {
                                updateLastMessage(list.get(0),contentTv,timeTv);
                            }

                        }
                    });
                }

            }
        });
    }

    private void updateLastMessage(AVIMMessage message,TextView contentTv,TextView timeTv) {
        if(null != message) {
            Date date = new Date(message.getTimestamp());
            SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
            timeTv.setText(format.format(date));
            contentTv.setText(getMessageeShorthand(HiTalkApplication.mAppContext, message));
        }
    }

    private static CharSequence getMessageeShorthand(Context context, AVIMMessage message) {
        if(message instanceof AVIMTypedMessage) {
            AVIMReservedMessageType type = AVIMReservedMessageType.getAVIMReservedMessageType(((AVIMTypedMessage)message).getMessageType());
            switch(type) {
                case TextMessageType:
                    return ((AVIMTextMessage)message).getText();
                case ImageMessageType:
                    return context.getString(R.string.message_shorthand_image);
                case LocationMessageType:
                    return context.getString(R.string.message_shorthand_location);
                case AudioMessageType:
                    return context.getString(R.string.message_shorthand_audio);
                default:
                    return context.getString(R.string.message_shorthand_unknown);
            }
        } else {
            return message.getContent();
        }
    }



    public class AtMessageHolder extends MessageHolder{

        public AtMessageHolder(View itemView) {
            super(itemView);
        }
    }

    public class BookMessageHolder extends MessageHolder{

        public BookMessageHolder(View itemView) {
            super(itemView);
        }
    }
}

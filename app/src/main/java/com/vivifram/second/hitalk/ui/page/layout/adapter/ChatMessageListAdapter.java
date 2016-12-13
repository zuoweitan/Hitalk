package com.vivifram.second.hitalk.ui.page.layout.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.vivifram.second.hitalk.bean.IMessageWrap;
import com.vivifram.second.hitalk.ui.page.layout.OnChatItemClickListener;
import com.vivifram.second.hitalk.ui.page.layout.chat.HiChatRowLayout;
import com.vivifram.second.hitalk.ui.page.layout.chat.HiChatRowTextLayout;
import com.zuowei.utils.common.TagUtil;
import com.zuowei.utils.helper.ChatHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zuowei on 16-8-2.
 */
public class ChatMessageListAdapter<T extends AVIMTypedMessage> extends BaseAdapter{

    public interface MessageFetcher<T extends AVIMTypedMessage>{
        List<IMessageWrap<T>> fetchMessages();
    }
    private final static String TAG = TagUtil.makeTag(ChatMessageListAdapter.class);
    private final static int MAXCOUNTS = 100;

    private Context mCtx;

    private static final int HANDLER_MESSAGE_REFRESH_LIST = 0;
    private static final int HANDLER_MESSAGE_SELECT_LAST = 1;
    private static final int HANDLER_MESSAGE_SEEK_TO = 2;

    private static final int MESSAGE_TYPE_RECV_TXT = 0;
    private static final int MESSAGE_TYPE_SENT_TXT = 1;
    private static final int MESSAGE_TYPE_SENT_IMAGE = 2;
    private static final int MESSAGE_TYPE_SENT_LOCATION = 3;
    private static final int MESSAGE_TYPE_RECV_LOCATION = 4;
    private static final int MESSAGE_TYPE_RECV_IMAGE = 5;
    private static final int MESSAGE_TYPE_SENT_VOICE = 6;
    private static final int MESSAGE_TYPE_RECV_VOICE = 7;
    private static final int MESSAGE_TYPE_SENT_VIDEO = 8;
    private static final int MESSAGE_TYPE_RECV_VIDEO = 9;
    private static final int MESSAGE_TYPE_SENT_FILE = 10;
    private static final int MESSAGE_TYPE_RECV_FILE = 11;

    private List<IMessageWrap<T>> messages_;
    private MessageFetcher<T> mMessageFetcher;

    private OnChatItemClickListener mIteamClickListener;
    private IChatRowProvider mRowProvider;

    private Drawable myBubbleBg;
    private Drawable otherBuddleBg;

    private ListView mHost;

    public ChatMessageListAdapter(Context context, ListView listView) {
        mCtx = context;
        mHost = listView;
        messages_ = new ArrayList<>();
    }

    Handler handler = new Handler() {

        private void refreshList() {
            if (mMessageFetcher != null) {
                List<IMessageWrap<T>> iMessageWraps = mMessageFetcher.fetchMessages();
                if (iMessageWraps != null && iMessageWraps.size() > 0){
                    IMessageWrap<T> first = iMessageWraps.get(0);
                    if (first.mHistory){
                        for (int i = iMessageWraps.size() - 1; i >= 0; i--) {
                            messages_.add(0,iMessageWraps.get(i));
                        }
                    }else {
                        for (int i = 0; i < iMessageWraps.size(); i++) {
                            messages_.add(iMessageWraps.get(i));
                        }
                    }
                }
            }
            notifyDataSetChanged();
        }

        @Override
        public void handleMessage(android.os.Message message) {
            switch (message.what) {
                case HANDLER_MESSAGE_REFRESH_LIST:
                    refreshList();
                    break;
                case HANDLER_MESSAGE_SELECT_LAST:
                    if (messages_.size() > 0) {
                        mHost.setSelection(messages_.size() - 1);
                    }
                    break;
                case HANDLER_MESSAGE_SEEK_TO:
                    int position = message.arg1;
                    mHost.setSelection(position);
                    break;
                default:
                    break;
            }
        }
    };

    public void refresh() {
        android.os.Message msg = handler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST);
        handler.sendMessage(msg);
    }

    public void refreshSelectLast() {
        // avoid refresh too frequently when receiving large amount offline messages
        final int TIME_DELAY_REFRESH_SELECT_LAST = 100;
        handler.removeMessages(HANDLER_MESSAGE_SELECT_LAST);
        handler.sendEmptyMessageDelayed(HANDLER_MESSAGE_REFRESH_LIST, TIME_DELAY_REFRESH_SELECT_LAST);
        handler.sendEmptyMessageDelayed(HANDLER_MESSAGE_SELECT_LAST, TIME_DELAY_REFRESH_SELECT_LAST);
    }

    public void refreshSeekTo(int position) {
        handler.sendMessage(handler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST));
        android.os.Message msg = handler.obtainMessage(HANDLER_MESSAGE_SEEK_TO);
        msg.arg1 = position;
        handler.sendMessage(msg);
    }

    public void clearMessages() {
        messages_.clear();
    }

    public void clearMessagesAndRefresh(){
        messages_.clear();
        notifyDataSetChanged();
    }

    public void setMessageFetcher(MessageFetcher<T> messageFetcher) {
        this.mMessageFetcher = messageFetcher;
    }

    public IMessageWrap<T> getItem(int position) {
        if (messages_ != null && position < messages_.size()) {
            return messages_.get(position);
        }
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    public int getCount() {
        return messages_ == null ? 0 : messages_.size();
    }

    public int getViewTypeCount() {
        if(mRowProvider != null && mRowProvider.getChatRowTypeCount() > 0){
            return mRowProvider.getChatRowTypeCount() + 12;
        }
        return 12;
    }


    public int getItemViewType(int position) {
        IMessageWrap<T> message = getItem(position);
        AVIMTypedMessage avimMessage = message.message_;
        if (message == null) {
            return -1;
        }

        if(mRowProvider != null && mRowProvider.getChatRowType(message.message_) > 0){
            return mRowProvider.getChatRowType(message.message_) + 11;
        }

        if (avimMessage.getMessageType() == IMessageWrap.MESSAGE_TYPE_TEXT) {
            return !ChatHelper.getInstance().isMe(avimMessage) ? MESSAGE_TYPE_RECV_TXT : MESSAGE_TYPE_SENT_TXT;
        }
        return -1;// invalid
    }

    protected HiChatRowLayout<T> createChatRow(Context context, IMessageWrap<T> message, int position) {
        HiChatRowLayout chatRow = null;
        if(mRowProvider != null && mRowProvider.getChatRow(message, position, this) != null){
            return mRowProvider.getChatRow(message, position, this);
        }
        switch (message.message_.getMessageType()) {
            case IMessageWrap.MESSAGE_TYPE_TEXT:
                chatRow = new HiChatRowTextLayout(mCtx, (IMessageWrap<AVIMTextMessage>) message,
                        position,(ChatMessageListAdapter<AVIMTextMessage>) this);
                break;
        }

        return chatRow;
    }


    @SuppressLint("NewApi")
    public View getView(final int position, View convertView, ViewGroup parent) {
        IMessageWrap<T> message = getItem(position);
        if(convertView == null){
            convertView = createChatRow(mCtx, message, position);
        }
        ((HiChatRowLayout<T>)convertView).setUpView(message, position, mIteamClickListener);

        return convertView;
    }

    public void setMyBubbleBg(Drawable myBubbleBg) {
        this.myBubbleBg = myBubbleBg;
    }


    public void setOtherBuddleBg(Drawable otherBuddleBg) {
        this.otherBuddleBg = otherBuddleBg;
    }


    public void setItemClickListener(OnChatItemClickListener listener){
        mIteamClickListener = listener;
    }

    public void setCustomChatRowProvider(IChatRowProvider rowProvider){
        mRowProvider = rowProvider;
    }

    public Drawable getMyBubbleBg() {
        return myBubbleBg;
    }


    public Drawable getOtherBuddleBg() {
        return otherBuddleBg;
    }
}

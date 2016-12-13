package com.vivifram.second.hitalk.ui.page.layout.chat;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.bean.IMessageWrap;
import com.vivifram.second.hitalk.ui.page.layout.OnChatItemClickListener;
import com.vivifram.second.hitalk.ui.page.layout.adapter.ChatMessageListAdapter;
import com.zuowei.utils.common.DateUtils;
import com.zuowei.utils.common.TagUtil;
import com.zuowei.utils.helper.ChatHelper;

import java.util.Date;

public abstract class HiChatRowLayout<T extends AVIMTypedMessage> extends LinearLayout
        implements IMessageWrap.OnIMessageStateChangedListener{
    protected static final String TAG = TagUtil.makeTag(HiChatRowLayout.class);

    protected LayoutInflater mInflater;
    protected Context mCtx;
    protected ChatMessageListAdapter<T> mAdapter;
    protected IMessageWrap message_;
    protected T mAvimMessage;
    protected int mPosition;

    protected TextView mTsTv;
    protected ImageView mUaiv;
    protected View mBlt;
    protected TextView mUntv;

    protected TextView mPercentageView;
    protected ProgressBar mProgressBar;
    protected ImageView mStatusView;
    protected Activity mActivity;

    protected TextView mAckedView;
    protected TextView mDeliveredView;

    protected OnChatItemClickListener itemClickListener;

    public HiChatRowLayout(Context context, IMessageWrap<T> message, int position, ChatMessageListAdapter<T> adapter) {
        super(context);
        mCtx = context;
        mActivity = (Activity) context;
        message_ = message;
        mAvimMessage = message.message_;
        mPosition = position;
        mAdapter = adapter;
        mInflater = LayoutInflater.from(context);

        initView();
    }

    private void initView() {
        onInflatView();
        mTsTv = (TextView) findViewById(R.id.timestamp);
        mUaiv = (ImageView) findViewById(R.id.iv_userhead);
        mBlt = findViewById(R.id.bubble);
        mUntv = (TextView) findViewById(R.id.tv_userid);

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mStatusView = (ImageView) findViewById(R.id.msg_status);
        mAckedView = (TextView) findViewById(R.id.tv_ack);
        mDeliveredView = (TextView) findViewById(R.id.tv_delivered);

        onFindViewById();
    }

    /**
     * 根据当前message和position设置控件属性等
     * 
     * @param message
     * @param position
     */
    public void setUpView(IMessageWrap<T> message, int position,
            OnChatItemClickListener itemClickListener) {
        message_ = message;
        mAvimMessage = message.message_;
        mPosition = position;
        this.itemClickListener = itemClickListener;

        setUpBaseView();
        onSetUpView();
        setClickListener();
    }

    private void setUpBaseView() {
        TextView timestamp = (TextView) findViewById(R.id.timestamp);
        if (timestamp != null) {
            if (mPosition == 0) {
                timestamp.setText(DateUtils.getTimestampString(new Date(mAvimMessage.getTimestamp())));
                timestamp.setVisibility(View.VISIBLE);
            } else {
                AVIMMessage prevMessage = mAdapter.getItem(mPosition - 1).message_;
                if (prevMessage != null && DateUtils.isCloseEnough(mAvimMessage.getTimestamp(), prevMessage.getTimestamp())) {
                    timestamp.setVisibility(View.GONE);
                } else {
                    timestamp.setText(DateUtils.getTimestampString(new Date(mAvimMessage.getTimestamp())));
                    timestamp.setVisibility(View.VISIBLE);
                }
            }
        }


        ChatHelper.setUserAvatar(mCtx, mAvimMessage.getFrom(), mUaiv);
        if(!ChatHelper.getInstance().isMe(mAvimMessage)){
            ChatHelper.setUserNick(mAvimMessage.getFrom(), mUntv);
        }

        if (mDeliveredView != null){
            mDeliveredView.setVisibility(View.INVISIBLE);
        }

        if (mAckedView != null) {
            mAckedView.setVisibility(View.INVISIBLE);
        }

        mUaiv.setVisibility(View.VISIBLE);
        if (mUntv != null) {
            mUntv.setVisibility(View.VISIBLE);
        }

        checkMessageState();

        if (mAdapter instanceof ChatMessageListAdapter) {
            if (ChatHelper.getInstance().isMe(mAvimMessage)) {
                if (((ChatMessageListAdapter) mAdapter).getMyBubbleBg() != null) {
                    mBlt.setBackgroundDrawable(((ChatMessageListAdapter) mAdapter).getMyBubbleBg());
                }
            }else {
                if (((ChatMessageListAdapter) mAdapter).getOtherBuddleBg() != null) {
                    mBlt.setBackgroundDrawable(((ChatMessageListAdapter) mAdapter).getOtherBuddleBg());
                }
            }
        }
    }

    private void checkMessageState() {
        switch (message_.message_.getMessageStatus()){
            case AVIMMessageStatusSending:
                    setVisibleState(mProgressBar,VISIBLE);
                    setVisibleState(mStatusView,GONE);
                break;
            case AVIMMessageStatusFailed:
                    setVisibleState(mProgressBar,GONE);
                    setVisibleState(mStatusView,VISIBLE);
                break;
            case AVIMMessageStatusReceipt:
            case AVIMMessageStatusSent:
            default:
                    setVisibleState(mProgressBar,GONE);
                    setVisibleState(mStatusView,GONE);
                break;
        }
    }

    private void setVisibleState(View v , int visible){
        if (v != null) {
            v.setVisibility(visible);
        }
    }



    private void setClickListener() {
        if(mBlt != null){
            mBlt.setOnClickListener(new OnClickListener() {
    
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null){
                        if(!itemClickListener.onBubbleClick(mAvimMessage)){
                            //如果listener返回false不处理这个事件，执行lib默认的处理
                            onBubbleClick();
                        }
                    }
                }
            });

            mBlt.setOnLongClickListener(new OnLongClickListener() {
    
                @Override
                public boolean onLongClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onBubbleLongClick(mAvimMessage);
                    }
                    return true;
                }
            });
        }

        if (mStatusView != null) {
            mStatusView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onResendClick(mAvimMessage);
                    }
                }
            });
        }

        mUaiv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onUserAvatarClick(mAvimMessage.getFrom());
                }
            }
        });
    }

    public void setOnIMessageStateChangedListener(){
        message_.onIMessageStateChangedListener_ =  this;
    }

    /**
     * 填充layout
     */
    protected abstract void onInflatView();

    /**
     * 查找chatrow里的控件
     */
    protected abstract void onFindViewById();

    /**
     * 设置更新控件属性
     */
    protected abstract void onSetUpView();
    
    /**
     * 聊天气泡被点击事件
     */
    protected abstract void onBubbleClick();
}

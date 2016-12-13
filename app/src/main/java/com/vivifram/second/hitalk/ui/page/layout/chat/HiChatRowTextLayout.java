package com.vivifram.second.hitalk.ui.page.layout.chat;

import android.content.Context;
import android.text.Spannable;
import android.view.View;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.bean.IMessageWrap;
import com.vivifram.second.hitalk.ui.page.layout.adapter.ChatMessageListAdapter;
import com.zuowei.utils.bridge.handler.ToolKit;
import com.zuowei.utils.common.SmileUtils;
import com.zuowei.utils.helper.ChatHelper;

public class HiChatRowTextLayout extends HiChatRowLayout<AVIMTextMessage>{

	private TextView mContentView;

    public HiChatRowTextLayout(Context context, IMessageWrap<AVIMTextMessage> message,
                               int position, ChatMessageListAdapter<AVIMTextMessage> adapter) {
        super(context, message, position, adapter);
    }


    @Override
	protected void onInflatView() {
		mInflater.inflate(!ChatHelper.getInstance().isMe(mAvimMessage) ?
				R.layout.hichat_row_received_message : R.layout.hichat_row_sent_message, this);
	}

	@Override
	protected void onFindViewById() {
        mContentView = (TextView) findViewById(R.id.tv_chatcontent);
	}

    @Override
    public void onSetUpView() {
        Spannable span = SmileUtils.getSmiledText(mCtx, mAvimMessage.getText());
        mContentView.setText(span, BufferType.SPANNABLE);
        handleTextMessage();
    }

    protected void handleTextMessage() {
        if (ChatHelper.getInstance().isMe(mAvimMessage)) {
            setOnIMessageStateChangedListener();
        }
    }

    @Override
    public void onIMessageStateChanged(final AVIMMessage.AVIMMessageStatus status) {
        ToolKit.runOnMainThreadSync(new Runnable() {
            @Override
            public void run() {
                switch (status) {
                    case AVIMMessageStatusSent:
                        mProgressBar.setVisibility(View.GONE);
                        mStatusView.setVisibility(View.GONE);
                        break;
                    case AVIMMessageStatusFailed:
                        mProgressBar.setVisibility(View.GONE);
                        mStatusView.setVisibility(View.VISIBLE);
                        break;
                    case AVIMMessageStatusSending:
                        mProgressBar.setVisibility(View.VISIBLE);
                        mStatusView.setVisibility(View.GONE);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    protected void onBubbleClick() {

    }
}

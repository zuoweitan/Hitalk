package com.vivifram.second.hitalk.ui.page.layout;

import android.view.View;
import android.widget.ListView;

import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.bean.IMessageWrap;
import com.vivifram.second.hitalk.ui.page.layout.adapter.ChatMessageListAdapter;
import com.vivifram.second.hitalk.ui.springview.container.DefaultFooter;
import com.vivifram.second.hitalk.ui.springview.container.HitalkRotationHeader;
import com.vivifram.second.hitalk.ui.springview.widget.SpringView;

/**
 * Created by zuowei on 16-8-2.
 */
public class ChatMessageListLayout extends BaseFragmentLayout
        implements SpringView.OnFreshListener{



    public interface IMessageListFreshListener{
        void onRefresh();
        void onLoadmore();
    }

    private SpringView mMessageSv;
    private ListView mMessagLv;
    private ChatMessageListAdapter mMessageListAdapter;
    private IMessageListFreshListener mMessageListFreshListener;
    public ChatMessageListLayout(View root) {
        super(root);
    }

    @Override
    public void onViewCreate(View root) {
        super.onViewCreate(root);
        mMessageSv = (SpringView) findViewById(R.id.messageSv);
        mMessagLv = (ListView) findViewById(R.id.messageLv);
        initSv();
        bindData();
    }

    private void initSv() {
        mMessageSv.setListener(this);
        mMessageSv.setHeader(new HitalkRotationHeader(mCtx));
        mMessageSv.setFooter(new DefaultFooter());
        mMessageSv.setGive(SpringView.Give.TOP);
        mMessageSv.setType(SpringView.Type.FOLLOW);
    }

    private void bindData() {
        mMessageListAdapter = new ChatMessageListAdapter(mCtx,mMessagLv);
        mMessagLv.setAdapter(mMessageListAdapter);
    }

    public <T extends AVIMTypedMessage>void setMessageFetcher(ChatMessageListAdapter.MessageFetcher<T> messageFetcher){
        mMessageListAdapter.setMessageFetcher(messageFetcher);
    }

    public void refreshMessageList(){
        mMessageListAdapter.refresh();
    }

    public void refreshSelectLast(){
        mMessageListAdapter.refreshSelectLast();
    }

    public void refreshSeekTo(int position){
        mMessageListAdapter.refreshSeekTo(position);
    }

    public void postToList(Runnable runnable){
        mMessagLv.post(runnable);
    }

    public void setMessageListFreshListener(IMessageListFreshListener mMessageListFreshListener) {
        this.mMessageListFreshListener = mMessageListFreshListener;
    }

    public void clear() {
        if (mMessageListAdapter != null) {
            mMessageListAdapter.clearMessages();
        }
    }

    public IMessageWrap getLastMessage(){
        if (mMessageListAdapter.getCount() > 0) {
            return mMessageListAdapter.getItem(mMessageListAdapter.getCount() - 1);
        }
        return null;
    }

    public IMessageWrap getFirstMessage() {
        if (mMessageListAdapter.getCount() > 0) {
            return mMessageListAdapter.getItem(0);
        }
        return null;
    }

    public IMessageWrap findMessageWrap(AVIMTypedMessage message){
        if (mMessageListAdapter.getCount() > 0){
            for (int count = mMessageListAdapter.getCount()-1; count >= 0; count--) {
                IMessageWrap iMessageWrap = mMessageListAdapter.getItem(count);
                if (iMessageWrap.message_.equals(message)){
                    return iMessageWrap;
                }
            }
        }
        return null;
    }

    @Override
    public void onViewDestroy() {
        super.onViewDestroy();
    }

    @Override
    public void onRefresh() {
        if (mMessageListFreshListener != null) {
            mMessageListFreshListener.onRefresh();
        }else {
            mMessageSv.onFinishFreshAndLoad();
        }
    }

    @Override
    public void onLoadmore() {
        if (mMessageListFreshListener != null) {
            mMessageListFreshListener.onLoadmore();
        }
    }

    public void finshRefresh(){
        mMessageSv.onFinishFreshAndLoad();
    }

    public void setOntouchListener(View.OnTouchListener ontouchListener){
        mMessagLv.setOnTouchListener(ontouchListener);
    }

}

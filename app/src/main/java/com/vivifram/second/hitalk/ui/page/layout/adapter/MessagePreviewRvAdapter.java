package com.vivifram.second.hitalk.ui.page.layout.adapter;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.vivifram.second.hitalk.R;

import java.util.ArrayList;
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
        switch (position){
            case TYPE_NORMAL:
                return conversationList.get(position);
        }

        return null;
    }

    @Override
    public int getItemCount() {
        return conversationList.size() + SPECIAL_ITEMS;
    }

    public static class MessageHolder<T> extends RecyclerView.ViewHolder{
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

    public static class NormalMessageHolder extends MessageHolder<AVIMConversation>{
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
                            } else {
                            }

                        }
                    });
                } else {
                }

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

    public static class AtMessageHolder extends MessageHolder{

        public AtMessageHolder(View itemView) {
            super(itemView);
        }
    }

    public static class BookMessageHolder extends MessageHolder{

        public BookMessageHolder(View itemView) {
            super(itemView);
        }
    }
}

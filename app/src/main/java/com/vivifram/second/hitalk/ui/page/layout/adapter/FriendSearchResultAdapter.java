package com.vivifram.second.hitalk.ui.page.layout.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.bean.search.FriendSearchItem;

/**
 * Created by zuowei on 17-1-8.
 */

public class FriendSearchResultAdapter extends ArrayRecyclerAdapter<FriendSearchItem,FriendSearchResultAdapter.FriendSearchItemHolder>{


    @Override
    public FriendSearchItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FriendSearchItemHolder(LayoutInflater.
                from(parent.getContext()).inflate(R.layout.friend_search_result_item,parent));
    }

    @Override
    public void onBindViewHolder(FriendSearchItemHolder holder, int position) {
        holder.bindData(getItem(position));
    }

    public static class FriendSearchItemHolder extends RecyclerView.ViewHolder{

        private ImageView avatarImv;
        private TextView nickTv;
        private TextView collegeTv;

        public FriendSearchItemHolder(View itemView) {
            super(itemView);

            avatarImv = (ImageView) itemView.findViewById(R.id.avatarImv);
            nickTv = (TextView) itemView.findViewById(R.id.nickTv);
            collegeTv = (TextView) itemView.findViewById(R.id.sInfo);
        }

        public void bindData(FriendSearchItem friendSearchItem){
            Glide.with(itemView.getContext())
                    .load(friendSearchItem.avatar)
                    .placeholder(R.drawable.default_avatar)
                    .into(avatarImv);

            nickTv.setText(friendSearchItem.nickName);
            collegeTv.setText(friendSearchItem.college);
        }

    }
}

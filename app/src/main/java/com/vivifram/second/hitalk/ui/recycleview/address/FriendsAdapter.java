package com.vivifram.second.hitalk.ui.recycleview.address;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jiang.android.lib.adapter.BaseAdapter;
import com.jiang.android.lib.adapter.expand.StickyRecyclerHeadersAdapter;
import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.bean.address.Friend;

/**
 * Created by zuowei on 16-10-13.
 */

public class FriendsAdapter extends BaseAdapter<Friend,RecyclerView.ViewHolder> implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {

    public static final int HEADER = 0X01;
    public static final int NORMAL = 0x02;
    private static final int HEADSIZE = 1;

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEADER;
        }
        return NORMAL;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = null;
        if (viewType == HEADER) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.friendheader, parent, false);
            return new RecyclerView.ViewHolder(view) {};
        }

        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friend_item, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position != 0 && holder instanceof FriendViewHolder) {
            ((FriendViewHolder)holder).initWithModel(getItem(position));
        }
    }

    @Override
    public long getHeaderId(int position) {
        if (position == 0){
            return position;
        }
        return getItem(position).getSortLetters().charAt(0);
    }

    @Override
    public void notifyDataAddChanged(int position) {
        if (position == -1){
            notifyItemInserted(getItemCount() - 1);
        }else if (position == 0){
            notifyDataSetChanged();
        }else {
            notifyItemInserted(position);
            notifyItemRangeChanged(position,super.getItemCount() - position);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friend_stickheader, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        TextView textView = (TextView) holder.itemView;
        String showValue = String.valueOf(getItem(position).getSortLetters().charAt(0));
        textView.setText(showValue);
    }

    private int getAdjustPosition(int position) {
        return position - HEADSIZE;
    }

    @Override
    public Friend getItem(int position) {
        if (position == 0){
            return null;
        }
        return super.getItem(getAdjustPosition(position));
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + 1;
    }

    @Override
    public boolean isRecyclerViewHeader(int position) {
        return position == 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateRecyclerHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friend_recyclerheader, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindRecyclerHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public boolean isRecyclerViewFooter(int position) {
        return false;
    }

    public class FriendViewHolder extends RecyclerView.ViewHolder{

        TextView nickNameTv;

        public FriendViewHolder(View itemView) {
            super(itemView);
            nickNameTv = (TextView) itemView.findViewById(R.id.nickTv);
        }

        public void initWithModel(Friend friend){
            nickNameTv.setText(friend.getNickName());
        }
    }
}

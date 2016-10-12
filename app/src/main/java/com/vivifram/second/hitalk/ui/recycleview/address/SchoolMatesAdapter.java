package com.vivifram.second.hitalk.ui.recycleview.address;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jiang.android.lib.adapter.BaseAdapter;
import com.jiang.android.lib.adapter.expand.StickyRecyclerHeadersAdapter;
import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.bean.address.SchoolMate;

/**
 * Created by zuowei on 16-10-13.
 */

public class SchoolMatesAdapter extends BaseAdapter<SchoolMate,RecyclerView.ViewHolder> implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {

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
                    .inflate(R.layout.schoolmateheader, parent, false);
            return new RecyclerView.ViewHolder(view) {};
        }

        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.schoolmate_item, parent, false);
        return new SchoolMateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SchoolMateViewHolder) {
            ((SchoolMateViewHolder)holder).initWithModel(getItem(position));
        }
    }

    @Override
    public long getHeaderId(int position) {
        if (position == 0){
            return -1;
        }
        return getItem(position).getSortLetters().charAt(0);
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.school_stickheader, parent, false);
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
    public SchoolMate getItem(int position) {
        if (position == 0){
            return new SchoolMate();
        }
        return super.getItem(getAdjustPosition(position));
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + 1;
    }

    public class SchoolMateViewHolder extends RecyclerView.ViewHolder{

        TextView nickNameTv;
        TextView infoTv;

        public SchoolMateViewHolder(View itemView) {
            super(itemView);
            nickNameTv = (TextView) itemView.findViewById(R.id.nickTv);
            infoTv = (TextView) itemView.findViewById(R.id.sInfo);
        }

        public void initWithModel(SchoolMate schoolMate){
            nickNameTv.setText(schoolMate.getNickName());
            infoTv.setText(schoolMate.getsInfo());
        }
    }
}

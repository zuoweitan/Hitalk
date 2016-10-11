package com.vivifram.second.hitalk.ui.page.layout.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vivifram.second.hitalk.R;

/**
 * Created by zuowei on 16-9-25.
 */
public class MessagePreviewRvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_AT = 0x01;
    private static final int TYPE_BOOK = 0x02;
    private static final int TYPE_NORMAL = 0x03;

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
        switch (getItemViewType(position)){
            case TYPE_AT:
                break;
            case TYPE_BOOK:
                break;
        }
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

    @Override
    public int getItemCount() {
        return 5;
    }

    public static class MessageHolder extends RecyclerView.ViewHolder{

        public MessageHolder(View itemView) {
            super(itemView);
        }
    }

    public static class NormalMessageHolder extends MessageHolder{

        public NormalMessageHolder(View itemView) {
            super(itemView);
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

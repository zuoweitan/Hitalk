package com.vivifram.second.hitalk.ui.page.layout.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.vivifram.second.hitalk.bean.search.FriendSearchItem;

/**
 * Created by zuowei on 16-12-25.
 */

public class FriendSearchSuggestionAdapter extends ArrayRecyclerAdapter<FriendSearchItem,FriendSearchSuggestionAdapter.SuggestionViewHolder> {


    @Override
    public SuggestionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(SuggestionViewHolder holder, int position) {

    }

    public static class SuggestionViewHolder extends RecyclerView.ViewHolder{

        public SuggestionViewHolder(View itemView) {
            super(itemView);
        }
    }
}

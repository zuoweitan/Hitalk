package com.vivifram.second.hitalk.ui.recycleview.blackboard;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class DivItemDecoration extends RecyclerView.ItemDecoration {
    private int mDivHeight;
    private boolean mHasHead;
    public DivItemDecoration(int divHeight, boolean hasHead){
        mDivHeight = divHeight;
        mHasHead = hasHead;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildAdapterPosition(view);
        if(mHasHead && position == 0){
            return;
        }
        outRect.bottom = mDivHeight;
    }
}
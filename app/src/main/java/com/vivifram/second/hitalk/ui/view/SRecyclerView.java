package com.vivifram.second.hitalk.ui.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;

import com.zuowei.utils.common.TagUtil;

/**
 * Created by zuowei on 16-10-15.
 */

public class SRecyclerView extends RecyclerView {

    public SRecyclerView(Context context) {
        super(context);
    }

    public SRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public float scrollVerticalOffset(){

        return computeVerticalScrollOffset();
    }
}

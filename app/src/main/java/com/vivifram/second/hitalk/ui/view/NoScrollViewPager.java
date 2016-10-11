package com.vivifram.second.hitalk.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.vivifram.second.hitalk.R;

/**
 * Created by zuowei on 16-8-2.
 */
public class NoScrollViewPager extends ViewPager {
    private boolean mScrollEnabled;
    public NoScrollViewPager(Context context) {
        super(context);
    }

    public NoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.NoScrollViewPager);
        mScrollEnabled = a.getBoolean(R.styleable.NoScrollViewPager_scroll_enable,false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return mScrollEnabled && super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mScrollEnabled && super.onInterceptTouchEvent(ev);
    }


    public void setScrollEnabled(boolean scrollEnabled){
        mScrollEnabled = scrollEnabled;
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item,false);
    }
}

package com.vivifram.second.hitalk.bean.blackboard;

import android.content.Context;
import android.graphics.drawable.Drawable;

/**
 * Created by zuowei on 16-8-12.
 */
public class SnsActionItem {
    // 定义图片对象
    public Drawable mDrawable;
    // 定义文本对象
    public CharSequence mTitle;

    public SnsActionItem(Drawable drawable, CharSequence title) {
        this.mDrawable = drawable;
        this.mTitle = title;
    }

    public SnsActionItem(CharSequence title) {
        this.mDrawable = null;
        this.mTitle = title;
    }

    public SnsActionItem(Context context, int titleId, int drawableId) {
        this.mTitle = context.getResources().getText(titleId);
        this.mDrawable = context.getResources().getDrawable(drawableId);
    }

    public SnsActionItem(Context context, CharSequence title, int drawableId) {
        this.mTitle = title;
        this.mDrawable = context.getResources().getDrawable(drawableId);
    }

    public void setItemTv(CharSequence tv) {
        mTitle = tv;
    }
}

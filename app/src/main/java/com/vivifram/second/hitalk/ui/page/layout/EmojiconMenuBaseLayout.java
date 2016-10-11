package com.vivifram.second.hitalk.ui.page.layout;

import com.vivifram.second.hitalk.bean.Emojicon;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class EmojiconMenuBaseLayout extends LinearLayout{
    protected EmojiconMenuListener listener;
    
    public EmojiconMenuBaseLayout(Context context) {
        super(context);
    }
    
    @SuppressLint("NewApi")
    public EmojiconMenuBaseLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    public EmojiconMenuBaseLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    
    /**
     * 设置回调监听
     * @param listener
     */
    public void setEmojiconMenuListener(EmojiconMenuListener listener){
        this.listener = listener;
    }
    
    public interface EmojiconMenuListener{
        /**
         * 表情被点击
         * @param emojicon
         */
        void onExpressionClicked(Emojicon emojicon);
        /**
         * 删除按钮被点击
         */
        void onDeleteImageClicked();
    }
}

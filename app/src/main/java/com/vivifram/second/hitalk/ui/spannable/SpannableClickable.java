package com.vivifram.second.hitalk.ui.spannable;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.vivifram.second.hitalk.HiTalkApplication;
import com.vivifram.second.hitalk.R;

public abstract class SpannableClickable extends ClickableSpan implements View.OnClickListener {

    private int DEFAULT_COLOR_ID = R.color.color_8290af;
    /**
     * text颜色
     */
    private int textColor ;

    public SpannableClickable() {
        this.textColor = HiTalkApplication.mAppContext.getResources().getColor(DEFAULT_COLOR_ID);
    }

    public SpannableClickable(int textColor){
        this.textColor = textColor;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);

        ds.setColor(textColor);
        ds.setUnderlineText(false);
        ds.clearShadowLayer();
    }
}
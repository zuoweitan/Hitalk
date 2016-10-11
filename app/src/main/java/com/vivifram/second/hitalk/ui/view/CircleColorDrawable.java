package com.vivifram.second.hitalk.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;

/**
 * 项目名称：GeakMessageCenter
 * 类描述：
 * 创建人：zuowei
 * 创建时间：15-9-18 下午3:49
 * 修改人：zuowei
 * 修改时间：15-9-18 下午3:49
 * 修改备注：
 */
public class CircleColorDrawable extends ColorDrawable {

    private final Paint mPaint = new Paint();
    private int mColor;
    private int[] mCenter;
    private int mRadius;
    public CircleColorDrawable(Context context, int color){
        mColor = color;
        mCenter = new int[2];

    }
    @Override
    public void draw(Canvas canvas) {
        mPaint.setColor(mColor);
        canvas.drawCircle(mCenter[0],mCenter[1],mRadius,mPaint);
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        mCenter[0] = (left + (right - left) / 2);
        mCenter[1] = (top + (bottom - top) / 2);
        mRadius = Math.min(mCenter[0],mCenter[1]);
    }
}

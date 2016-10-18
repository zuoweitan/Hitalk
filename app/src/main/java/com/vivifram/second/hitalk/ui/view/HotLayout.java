package com.vivifram.second.hitalk.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.vivifram.second.hitalk.R;
import com.zuowei.utils.common.DisplayUtil;

/**
 * Created by zuowei on 16-10-18.
 */

public class HotLayout extends RelativeLayout {

    public HotLayout(Context context) {
        super(context);
    }

    public HotLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public HotLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
    }

    public HotLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttrs(attrs);
    }

    protected void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.HotLayout, 0, 0);
        try {
            hotTitle = typedArray.getString(R.styleable.HotLayout_hot_title);
        }finally {
            typedArray.recycle();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        init();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setLayerType(LAYER_TYPE_SOFTWARE,null);
    }

    private void init() {
        hotPath = new Path();
        int x1 =  DisplayUtil.dip2px(getContext(),290);
        int x2 = DisplayUtil.dip2px(getContext(),324);
        int x3 = DisplayUtil.dip2px(getContext(),307);
        int y1 = DisplayUtil.dip2px(getContext(),0);
        int y2 = DisplayUtil.dip2px(getContext(),24);
        int y3 = DisplayUtil.dip2px(getContext(),38);
        hotPath.moveTo(x1,y1);
        hotPath.lineTo(x2,y1);
        hotPath.lineTo(x2,y2);
        hotPath.lineTo(x3,y3);
        hotPath.lineTo(x1,y2);
        hotPath.close();

        lsPoint = new Point();
        lsPoint.set(x1 - 3,0);

        lePoint = new Point();
        lePoint.set(x2,0);

        rTitlePoint = new Point();
        rTitlePoint.set(x3, (y1 + y3 - 4)/2);

        hotPaint = new Paint();
        hotPaint.setColor(Color.RED);
        hotPaint.setShadowLayer(2,-2,-1,Color.DKGRAY);

        ColorMatrix colorMatrix = new ColorMatrix(new float[]{
                0.8f,0,0,0,0,
                0,0,0,0,0,
                0,0,0,0,0,
                0,0,0,1,0
        });
        lineFilter = new ColorMatrixColorFilter(colorMatrix);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        drawHot(canvas);
        drawTop(canvas);
        drawText(canvas);
    }



    private Point lsPoint;
    private Point lePoint;
    private ColorMatrixColorFilter lineFilter;

    private Path hotPath;
    private Paint hotPaint;

    String hotTitle;
    private Point rTitlePoint;
    private PointF titlePoint;

    private void drawHot(Canvas canvas) {
        canvas.drawPath(hotPath,hotPaint);
    }

    private void drawTop(Canvas canvas) {
        hotPaint.setColorFilter(lineFilter);
        hotPaint.setStrokeWidth(5);
        canvas.drawLine(lsPoint.x,lsPoint.y,lePoint.x,lePoint.y,hotPaint);
    }

    private void drawText(Canvas canvas) {
        if (hotTitle != null){
            hotPaint.setColor(Color.WHITE);
            hotPaint.setColorFilter(null);
            hotPaint.setStrokeWidth(2);
            hotPaint.setTypeface(Typeface.MONOSPACE);
            hotPaint.setTextSize(DisplayUtil.dip2px(getContext(),18));

            if (titlePoint == null){
                Paint.FontMetrics fontMetrics = hotPaint.getFontMetrics();
                float startX = rTitlePoint.x - hotPaint.measureText(hotTitle) / 2;
                float startY = rTitlePoint.y - (fontMetrics.descent + fontMetrics.ascent) / 2;
                titlePoint = new PointF();
                titlePoint.set(startX,startY);
            }

            canvas.drawText(hotTitle,titlePoint.x,titlePoint.y,hotPaint);
        }
    }
}


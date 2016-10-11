package com.zuowei.utils.common;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

/**
 * Created by zuowei on 16-7-28.
 */
public class TextSpanUtils {

        public static ImageSpan getTextWithBackground(Drawable background, final int textSize
                , final int padding, final int color){
            return new ImageSpan(background){
                @Override
                public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
                    paint.setTextSize(textSize + padding);
                    int len = Math.round(paint.measureText(text, start, end));
                    getDrawable().setBounds(0, 0, len, (int)  (1.5f * getTextMaxHeight(paint,textSize)));

                    Drawable b = getDrawable();
                    canvas.save();
                    int transY = ((bottom-top) - b.getBounds().bottom)/2+top;
                    canvas.translate(x , transY);
                    b.draw(canvas);
                    canvas.restore();

                    paint.setColor(color);
                    paint.setTextSize(textSize);
                    canvas.drawText(text.subSequence(start, end).toString(), x+ padding ,y, paint);
                }
            };
        }

        private static float getTextMaxHeight(Paint paint,float textSize){
            paint.setTextSize(textSize);
            Paint.FontMetrics fm = paint.getFontMetrics();
            return fm.bottom - fm.top;
        }

        private static float getTextHeight(Paint paint,float textSize){
            paint.setTextSize(textSize);
            Paint.FontMetrics fm = paint.getFontMetrics();
            return fm.descent - fm.ascent;
        }
}

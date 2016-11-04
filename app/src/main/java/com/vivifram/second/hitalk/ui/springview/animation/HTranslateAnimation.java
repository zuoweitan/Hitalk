package com.vivifram.second.hitalk.ui.springview.animation;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;

import com.zuowei.utils.common.NLog;
import com.zuowei.utils.common.TagUtil;

import java.lang.reflect.Field;

import static com.vivifram.second.hitalk.R.string.set;

/**
 * 项目名称：Hitalk
 * 类描述：
 * 创建人：zuowei
 * 创建时间：16-11-4 下午2:18
 * 修改人：zuowei
 * 修改时间：16-11-4 下午2:18
 * 修改备注：
 */
public class HTranslateAnimation extends TranslateAnimation{

    public interface OnTranslateListener extends AnimationListener{
        void onTranslateChanged(float dx,float dy);
    }

    private float mFromXDelta;
    private float mToXDelta;
    private float mFromYDelta;
    private float mToYDelta;

    OnTranslateListener onTranslateListener;

    public HTranslateAnimation(int fromXType, float fromXValue, int toXType, float toXValue, int fromYType, float fromYValue, int toYType, float toYValue) {
        super(fromXType, fromXValue, toXType, toXValue, fromYType, fromYValue, toYType, toYValue);
    }

    public HTranslateAnimation(float fromXDelta, float toXDelta, float fromYDelta, float toYDelta) {
        super(fromXDelta, toXDelta, fromYDelta, toYDelta);
    }

    public HTranslateAnimation(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void init() {
        Class<?> superclass = getClass().getSuperclass();
        try {
            Field field = superclass.getDeclaredField("mFromXDelta");
            field.setAccessible(true);
            mFromXDelta = (float) field.get(this);

            field = superclass.getDeclaredField("mToXDelta");
            field.setAccessible(true);
            mToXDelta = (float) field.get(this);

            field = superclass.getDeclaredField("mFromYDelta");
            field.setAccessible(true);
            mFromYDelta = (float) field.get(this);

            field = superclass.getDeclaredField("mToYDelta");
            field.setAccessible(true);
            mToYDelta = (float) field.get(this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setAnimationListener(AnimationListener listener) {
        if (listener instanceof OnTranslateListener){
            onTranslateListener = (OnTranslateListener) listener;
        }
        super.setAnimationListener(listener);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        float dx = mFromXDelta;
        float dy = mFromYDelta;
        if (mFromXDelta != mToXDelta) {
            dx = mFromXDelta + ((mToXDelta - mFromXDelta) * interpolatedTime);
        }
        if (mFromYDelta != mToYDelta) {
            dy = mFromYDelta + ((mToYDelta - mFromYDelta) * interpolatedTime);
        }

        if (onTranslateListener != null) {
            onTranslateListener.onTranslateChanged(dx,dy);
        }
        super.applyTransformation(interpolatedTime, t);
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        init();
    }

    @Override
    public String toString() {
        return "HTranslateAnimation{" +
                "mFromXDelta=" + mFromXDelta +
                ", mToXDelta=" + mToXDelta +
                ", mFromYDelta=" + mFromYDelta +
                ", mToYDelta=" + mToYDelta +
                '}';
    }
}

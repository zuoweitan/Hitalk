package com.vivifram.second.hitalk.ui.springview.container;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ProgressBar;

import com.vivifram.second.hitalk.R;
import com.zuowei.utils.common.DisplayUtil;

/**
 * Created by zuowei on 16-8-11.
 */
public class AddressRotationHeader extends BaseHeader {
    private Context context;
    private int rotationSrc;

    private RotateAnimation mRotateUpAnim;

    private ProgressBar progress;

    public AddressRotationHeader(Context context){
        this(context,0);
    }

    public AddressRotationHeader(Context context, int rotationSrc){
        this.context = context;
        this.rotationSrc = rotationSrc;

        mRotateUpAnim = new RotateAnimation(0.0f, 360.0f,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,0.5f);
        mRotateUpAnim.setInterpolator(new LinearInterpolator());
        mRotateUpAnim.setRepeatCount(Integer.MAX_VALUE);
        mRotateUpAnim.setDuration(600);
        mRotateUpAnim.setFillAfter(true);
    }

    @Override
    public View getView(LayoutInflater inflater,ViewGroup viewGroup) {
        View view = inflater.inflate(R.layout.rotation_overlap_header, viewGroup, true);
        progress = (ProgressBar) view.findViewById(R.id.progress);
        if (rotationSrc != 0) {
            progress.setIndeterminateDrawable(ContextCompat.getDrawable(context, rotationSrc));
        }
        return view;
    }

    @Override
    public int getDragLimitHeight(View rootView) {
        return rootView.getMeasuredHeight()/4;
    }

    @Override
    public void onPreDrag(View rootView) {
        progress.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDropAnim(View rootView, int dy) {
        int maxY = DisplayUtil.dip2px(context, 60);
        float y = maxY * Math.abs(dy)/rootView.getMeasuredHeight();
        if (y > maxY) return;
        rootView.setTranslationY(y);

        float rota = 360*dy/rootView.getMeasuredHeight();
        progress.setRotation(rota);
    }

    @Override
    public void onLimitDes(View rootView, boolean upORdown) {
    }

    @Override
    public void onStartAnim() {
        progress.startAnimation(mRotateUpAnim);
    }

    @Override
    public void onFinishAnim() {
        progress.clearAnimation();
        progress.setVisibility(View.GONE);
    }
}

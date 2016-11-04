package com.vivifram.second.hitalk.ui.springview.container;

import android.content.Context;
import android.graphics.Interpolator;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.BaseInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ProgressBar;

import com.vivifram.second.hitalk.R;
import com.zuowei.utils.common.DisplayUtil;
import com.zuowei.utils.common.NLog;
import com.zuowei.utils.common.TagUtil;

/**
 * Created by zuowei on 16-8-11.
 */
public class AddressRotationHeader extends BaseHeader {
    private Context context;
    private int rotationSrc;

    private RotateAnimation mRotateUpAnim;

    private ProgressBar progress;
    private View root;

    public AddressRotationHeader(Context context, RecyclerView recyclerView){
        this(context,0,recyclerView);
    }

    public AddressRotationHeader(Context context, int rotationSrc,RecyclerView recyclerView){
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
        root = inflater.inflate(R.layout.rotation_overlap_header, viewGroup, true);
        progress = (ProgressBar) root.findViewById(R.id.progress);
        if (rotationSrc != 0) {
            progress.setIndeterminateDrawable(ContextCompat.getDrawable(context, rotationSrc));
        }
        //progress.setVisibility(View.INVISIBLE);
        return root;
    }

    public void enable(boolean b){
        root.setVisibility(b?View.VISIBLE:View.GONE);
    }

    @Override
    public int getDragLimitHeight(View rootView) {
        return rootView.getMeasuredHeight()/3;
    }

    @Override
    public void onPreDrag(View rootView) {
        //progress.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDropAnim(View rootView, int dy) {
        int maxY = DisplayUtil.dip2px(context, 50);
        float y = (float) (maxY * Math.abs(dy)/rootView.getMeasuredHeight() * Math.pow(1.4f,2));
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
    public int getDragSpringHeight(View rootView) {
        return 135;
    }

    @Override
    public void onFinishAnim() {
        progress.clearAnimation();
        //progress.setVisibility(View.GONE);
    }

    @Override
    public void onPositionReset() {
        super.onPositionReset();
        //progress.setVisibility(View.GONE);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    class TInterpolator extends BaseInterpolator{

        @Override
        public float getInterpolation(float input) {
            return 0;
        }
    }
}

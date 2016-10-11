package com.vivifram.second.hitalk.ui.springview.container;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.vivifram.second.hitalk.R;
import com.zuowei.utils.common.DisplayUtil;


/**
 * Created by Administrator on 2016/3/21.
 */
public class HitalkRotationHeader extends BaseHeader {

    private AnimationDrawable animationPull;
    private AnimationDrawable animationPullFan;
    private AnimationDrawable animationRefresh;

    private Context context;
    private ImageView header_img;
    private int[] pullAnimSrcs = new int[]{R.drawable.loading_01,R.drawable.loading_02,R.drawable.loading_03,
            R.drawable.loading_04,R.drawable.loading_05,R.drawable.loading_06,R.drawable.loading_07,
            R.drawable.loading_08,R.drawable.loading_09,R.drawable.loading_10,R.drawable.loading_11,
            R.drawable.loading_12};
    private int[] refreshAnimSrcs = new int[]{R.drawable.loading_01,R.drawable.loading_02,R.drawable.loading_03,
            R.drawable.loading_04,R.drawable.loading_05,R.drawable.loading_06,R.drawable.loading_07,
            R.drawable.loading_08,R.drawable.loading_09,R.drawable.loading_10,R.drawable.loading_11,
            R.drawable.loading_12};

    public HitalkRotationHeader(Context context){
        this(context,null,null);
    }
    public HitalkRotationHeader(Context context,int[] pullAnimSrcs,int[] refreshAnimSrcs){
        this.context = context;
        if (pullAnimSrcs!=null) this.pullAnimSrcs = pullAnimSrcs;
        if (refreshAnimSrcs!=null) this.refreshAnimSrcs = refreshAnimSrcs;
        animationPull = new AnimationDrawable();
        animationPullFan = new AnimationDrawable();
        animationRefresh = new AnimationDrawable();
        for (int i=1;i< this.pullAnimSrcs.length;i++) {
            animationPull.addFrame(ContextCompat.getDrawable(context, this.pullAnimSrcs[i]),100);
            animationRefresh.setOneShot(true);
        }
        for (int i= this.pullAnimSrcs.length-1;i>=0;i--){
            animationPullFan.addFrame(ContextCompat.getDrawable(context, this.pullAnimSrcs[i]), 100);
            animationRefresh.setOneShot(true);
        }
        for (int src: this.refreshAnimSrcs) {
            animationRefresh.addFrame(ContextCompat.getDrawable(context, src),150);
            animationRefresh.setOneShot(false);
        }
    }

    @Override
    public View getView(LayoutInflater inflater,ViewGroup viewGroup) {
        View view = inflater.inflate(R.layout.hitalk_rotation_header, viewGroup, true);
        header_img = (ImageView) view.findViewById(R.id.hitalk_header_img);
        if (pullAnimSrcs !=null&& pullAnimSrcs.length>0)
            header_img.setImageResource(pullAnimSrcs[0]);
        return view;
    }

    @Override
    public void onPreDrag(View rootView) {
    }

    @Override
    public void onDropAnim(View rootView, int dy) {
        int maxw = DisplayUtil.dip2px(context, 45);
        float w = maxw*Math.abs(dy)/rootView.getMeasuredHeight();
        if (w>maxw) return;
        ViewGroup.LayoutParams layoutParams = header_img.getLayoutParams();
        layoutParams.width = (int) w;
        header_img.setLayoutParams(layoutParams);
    }

    @Override
    public void onLimitDes(View rootView, boolean upORdown) {
        if (!upORdown){
            header_img.setImageDrawable(animationPull);
            animationPull.start();
        }else {
            header_img.setImageDrawable(animationPullFan);
            animationPullFan.start();
        }
    }

    @Override
    public void onStartAnim() {
        header_img.setImageDrawable(animationRefresh);
        animationRefresh.start();
    }

    @Override
    public void onFinishAnim() {
        if (pullAnimSrcs !=null&& pullAnimSrcs.length>0)
            header_img.setImageResource(pullAnimSrcs[0]);
    }
}
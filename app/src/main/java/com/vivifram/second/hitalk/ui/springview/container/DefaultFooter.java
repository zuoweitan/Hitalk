package com.vivifram.second.hitalk.ui.springview.container;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vivifram.second.hitalk.R;

/**
 * Created by zuowei on 16-8-1.
 */
public class DefaultFooter extends BaseFooter {

    @Override
    public View getView(LayoutInflater inflater, ViewGroup viewGroup) {
        View view = inflater.inflate(R.layout.default_footer, viewGroup, true);
        return view;
    }

    @Override
    public void onPreDrag(View rootView) {

    }

    @Override
    public void onDropAnim(View rootView, int dy) {

    }

    @Override
    public void onLimitDes(View rootView, boolean upORdown) {

    }

    @Override
    public void onStartAnim() {

    }

    @Override
    public void onFinishAnim() {

    }
}

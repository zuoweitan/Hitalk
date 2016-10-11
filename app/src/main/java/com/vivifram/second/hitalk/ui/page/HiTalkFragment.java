package com.vivifram.second.hitalk.ui.page;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.base.LayoutInject;
import com.vivifram.second.hitalk.ui.page.layout.HitalkFragmentLayout;
import com.zuowei.utils.bridge.EaterManager;
import com.zuowei.utils.bridge.IEater;
import com.zuowei.utils.bridge.params.LightParam;

/**
 * Created by zuowei on 16-7-25.
 */
@LayoutInject(name = "HitalkFragmentLayout")
public class HiTalkFragment extends LazyFragment<HitalkFragmentLayout> {

    private Fragment[] mFragments;
    private HiTalkFragmentSub1 mHitalkFragmentSub1;
    private HiTalkFragmentSub2 mHitalkFragmentSub2;
    private int mIndex;
    private IEater mSquareCountEater;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mHitalkFragmentSub1 = new HiTalkFragmentSub1();
        mHitalkFragmentSub2 = new HiTalkFragmentSub2();
        mFragments = new Fragment[]{mHitalkFragmentSub1,mHitalkFragmentSub2};
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void onViewCreated() {
        super.onViewCreated();
        showContent();
        init();
        mSquareCountEater = new IEater() {
            @Override
            public void onEat(LightParam param) {
                if (param != null){
                    mLayout.setTabOneCount(param.arg1);
                }
            }
        };
        EaterManager.getInstance().registerEater("square_count",mSquareCountEater);
    }

    @Override
    protected void onViewDestroyed() {
        super.onViewDestroyed();
        EaterManager.getInstance().unRegisterEater(mSquareCountEater);
    }

    private void init() {
        mLayout.setOnTabClickListener(new HitalkFragmentLayout.OnTabClickListener() {
            @Override
            public void onTabClick(int index) {
                switchTab(index);
            }
        });
    }

    private void showContent() {

        getActivity().getSupportFragmentManager().beginTransaction()
                .add(mLayout.getContentId(), mHitalkFragmentSub1)
                .add(mLayout.getContentId(), mHitalkFragmentSub2)
                .hide(mHitalkFragmentSub2)
                .show(mHitalkFragmentSub1).commit();

        mIndex = 0;
    }

    private void switchTab(int index){
        if (index != mIndex) {
            FragmentTransaction trx = getActivity().getSupportFragmentManager()
                    .beginTransaction();
            trx.hide(mFragments[mIndex]);
            if (!mFragments[index].isAdded()) {
                trx.add(mLayout.getContentId(), mFragments[index]);
            }
            trx.show(mFragments[index]).commit();
        }

        mIndex = index;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_hi_layout;
    }


}

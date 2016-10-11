package com.vivifram.second.hitalk.ui.page;

import com.vivifram.second.hitalk.base.BaseFragment;
import com.vivifram.second.hitalk.ui.page.layout.BaseFragmentLayout;
import com.vivifram.second.hitalk.ui.page.layout.HitalkFragmentLayout;

/**
 * Created by zuowei on 16-7-25.
 */
public abstract class LazyFragment<T extends BaseFragmentLayout> extends BaseFragment<T> {

    protected boolean isVisible;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    protected boolean canLoadData(){
        return false;
    }

    protected void onVisible(){
        lazyLoad();
    }

    protected abstract void lazyLoad();

    protected void onInvisible(){}
}

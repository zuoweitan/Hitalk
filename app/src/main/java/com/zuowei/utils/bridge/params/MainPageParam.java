package com.zuowei.utils.bridge.params;

import com.zuowei.utils.bridge.constant.EaterAction;

/**
 * Created by zuowei on 17-3-26.
 */

public class MainPageParam extends LightParam {

    private int pageIndex;

    public MainPageParam() {
        super(EaterAction.ACTION_SET_MAIN_PAGE);
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public MainPageParam setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
        return this;
    }
}

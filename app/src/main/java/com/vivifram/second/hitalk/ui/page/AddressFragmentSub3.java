package com.vivifram.second.hitalk.ui.page;

import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.base.LayoutInject;
import com.vivifram.second.hitalk.ui.page.layout.AddressFragmentSub3Layout;

/**
 * Created by zuowei on 16-10-11.
 */
@LayoutInject(name = "AddressFragmentSub3Layout")
public class AddressFragmentSub3 extends LazyFragment<AddressFragmentSub3Layout> {
    @Override
    protected void lazyLoad() {

    }

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_address_sub_3_layout;
    }
}

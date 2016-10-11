package com.vivifram.second.hitalk.ui.page;

import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.base.LayoutInject;
import com.vivifram.second.hitalk.ui.page.layout.AddressFragmentSub1Layout;

/**
 * Created by zuowei on 16-10-11.
 */
@LayoutInject(name = "AddressFragmentSub1Layout")
public class AddressFragmentSub1 extends LazyFragment<AddressFragmentSub1Layout> {
    @Override
    protected void lazyLoad() {

    }

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_address_sub_1_layout;
    }
}

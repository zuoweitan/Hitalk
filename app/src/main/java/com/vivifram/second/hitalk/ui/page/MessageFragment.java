package com.vivifram.second.hitalk.ui.page;

import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.base.LayoutInject;
import com.vivifram.second.hitalk.ui.page.layout.MessageFragmentLayout;
import com.zuowei.utils.common.NLog;
import com.zuowei.utils.common.TagUtil;

/**
 * Created by zuowei on 16-7-25.
 */
@LayoutInject(name = "MessageFragmentLayout")
public class MessageFragment extends LazyFragment<MessageFragmentLayout> {
    @Override
    protected void lazyLoad() {
    }

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_message_layout;
    }
}

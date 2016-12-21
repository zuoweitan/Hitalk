package com.vivifram.second.hitalk.ui.page;

import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.base.LayoutInject;
import com.vivifram.second.hitalk.ui.page.layout.SearchFriendLayout;

/**
 * 项目名称：Hitalk
 * 类描述：
 * 创建人：zuowei
 * 创建时间：16-12-21 下午3:19
 * 修改人：zuowei
 * 修改时间：16-12-21 下午3:19
 * 修改备注：
 */
@LayoutInject(name = "SearchFriendLayout")
public class SearchFriendFragment extends LazyFragment<SearchFriendLayout>{

    @Override
    protected void lazyLoad() {

    }

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_search_friend_layout;
    }
}

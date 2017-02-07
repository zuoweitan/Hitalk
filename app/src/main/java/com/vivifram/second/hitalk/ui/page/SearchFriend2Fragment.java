package com.vivifram.second.hitalk.ui.page;

import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.base.InterfaceInject;
import com.vivifram.second.hitalk.base.LayoutInject;
import com.vivifram.second.hitalk.ui.page.layout.SearchFriend2Layout;

/**
 * Created by zuowei on 17-2-5.
 */

@LayoutInject(name = "SearchFriend2Layout")
public class SearchFriend2Fragment extends LazyFragment<SearchFriend2Layout> {
    @Override
    protected void lazyLoad() {

    }

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_search_friend_2_layout;
    }

    @InterfaceInject(bindName =  "onSearchBarListener")
    SearchFriend2Layout.OnSearchBarListener onSearchBarListener = new SearchFriend2Layout.OnSearchBarListener() {
        @Override
        public void onback() {
            getActivity().finish();
        }
    };
}

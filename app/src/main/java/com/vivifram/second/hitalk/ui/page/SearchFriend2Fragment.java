package com.vivifram.second.hitalk.ui.page;

import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.base.InterfaceInject;
import com.vivifram.second.hitalk.base.LayoutInject;
import com.vivifram.second.hitalk.bean.Constants;
import com.vivifram.second.hitalk.bean.address.SchoolMate;
import com.vivifram.second.hitalk.manager.chat.SchoolMatesManager;
import com.vivifram.second.hitalk.ui.FriendInfoActivity;
import com.vivifram.second.hitalk.ui.ParamsPool;
import com.vivifram.second.hitalk.ui.page.layout.SearchFriend2Layout;
import com.zuowei.utils.common.NLog;
import com.zuowei.utils.common.TagUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bolts.Continuation;
import bolts.Task;

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

        @Override
        public void onSearchStart(String s) {
            doSearch(s);
        }
    };

    private void doSearch(String s) {
        Map<String, Object> conditions = new HashMap<>();
        conditions.put(Constants.User.NICKNAME_C,s);
        SchoolMatesManager.getInstance().queryAllSchoolMates(conditions)
                .continueWith(task -> {
                    NLog.i(TagUtil.makeTag(getClass()),"error = "+task.getError()+", result = "+task.getResult());
                    if (task.getError() != null || task.getResult().size() <= 0){
                        mLayout.showNotFount(true);
                        mLayout.showSearchContent(false);
                    } else {
                        //do show detail info
                        int key = new Object().hashCode();
                        ParamsPool.$().put(key,task.getResult().get(0));
                        FriendInfoActivity.start(getActivity(),key);
                    }
                    return null;
                },Task.UI_THREAD_EXECUTOR);
    }
}

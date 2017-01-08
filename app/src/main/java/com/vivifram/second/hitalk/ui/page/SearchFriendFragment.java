package com.vivifram.second.hitalk.ui.page;

import android.os.Message;

import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.base.InterfaceInject;
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

    private static final int MSG_SEARCH_START = 0x01;

    {
        workHandler = new WorkHandler() {
            @Override
            public void safeHandler(Message msg) {
                super.safeHandler(msg);
                switch (msg.what) {
                    case MSG_SEARCH_START:
                        String key = (String) msg.obj;
                        requestOneShot(key);
                        break;
                }
            }
        };
    }

    private void requestOneShot(String key) {
        //// TODO: 17-1-8  
    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_search_friend_layout;
    }

    @InterfaceInject(bindName = "onSearchActionChanged")
    SearchFriendLayout.OnSearchActionChanged onSearchActionChanged = text -> {
        Message message = new Message();
        message.obj = text;
        message.what =  MSG_SEARCH_START;
        workHandler.removeMessages(MSG_SEARCH_START);
        workHandler.sendMessageDelayed(message,100);
    };
}

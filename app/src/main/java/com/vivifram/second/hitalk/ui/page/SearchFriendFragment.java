package com.vivifram.second.hitalk.ui.page;

import android.os.Message;

import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.base.InterfaceInject;
import com.vivifram.second.hitalk.base.LayoutInject;
import com.vivifram.second.hitalk.bean.address.SchoolMate;
import com.vivifram.second.hitalk.manager.chat.SchoolMatesManager;
import com.vivifram.second.hitalk.ui.page.layout.SearchFriendLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bolts.Continuation;
import bolts.Task;

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
        Map<String, Object> conditions = new HashMap<>();
        SchoolMatesManager.getInstance().queryAllSchoolMates(conditions)
                .continueWith(new Continuation<List<SchoolMate>, Object>() {
                    @Override
                    public Object then(Task<List<SchoolMate>> task) throws Exception {
                        return null;
                    }
                });
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

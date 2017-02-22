package com.vivifram.second.hitalk.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.SparseArray;

import com.avos.avoscloud.AVCallback;
import com.avos.avoscloud.AVException;
import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.base.BaseActivity;
import com.vivifram.second.hitalk.base.LayoutInject;
import com.vivifram.second.hitalk.bean.Constants;
import com.vivifram.second.hitalk.ui.layout.ChatRoomLayout;
import com.zuowei.dao.greendao.User;
import com.zuowei.utils.common.NToast;
import com.zuowei.utils.helper.UserBeanCacheHelper;

/**
 * 项目名称：Hitalk
 * 类描述：
 * 创建人：zuowei
 * 创建时间：17-2-20 上午10:49
 * 修改人：zuowei
 * 修改时间：17-2-20 上午10:49
 * 修改备注：
 */

@LayoutInject(name = "ChatRoomLayout")
    public class ChatRoomActivity extends BaseActivity<ChatRoomLayout>{

    public static void start(Context c,int key){
        start(c,ChatRoomActivity.class,key);
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_chat_room_layout);
        SparseArray chatParams = (SparseArray) params;
        switch (chatParams.keyAt(0)){
            case Constants.ParamsKey.Chat.TO_FRIEND:
                    String toUserId = (String) chatParams.get(Constants.ParamsKey.Chat.TO_FRIEND);
                    UserBeanCacheHelper.getInstance().getCachedUser(toUserId, new AVCallback<User>() {
                        @Override
                        protected void internalDone0(User user, AVException e) {
                            if (e != null) {
                                doInitChatRoom(user);
                            } else {
                                NToast.shortToast(mAppCtx,getResources().getString(R.string.open_chat_room_failed));
                            }
                        }
                    });
                break;
        }
    }

    private void doInitChatRoom(User user) {
        mLayout.setTitle(user.getNick());
        
    }
}

package com.vivifram.second.hitalk.ui;

import android.content.Context;
import android.os.Bundle;

import com.afollestad.materialdialogs.MaterialDialog;
import com.avos.avoscloud.AVUser;
import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.base.BaseActivity;
import com.vivifram.second.hitalk.base.InterfaceInject;
import com.vivifram.second.hitalk.base.LayoutInject;
import com.vivifram.second.hitalk.bean.address.SchoolMate;
import com.vivifram.second.hitalk.manager.chat.FriendsManager;
import com.vivifram.second.hitalk.ui.layout.FriendInfoLayout;
import com.zuowei.utils.common.NLog;
import com.zuowei.utils.common.NToast;
import com.zuowei.utils.common.TagUtil;
import com.zuowei.utils.helper.UserCacheHelper;

import bolts.Task;

/**
 * 项目名称：Hitalk
 * 类描述：
 * 创建人：zuowei
 * 创建时间：16-12-14 下午3:14
 * 修改人：zuowei
 * 修改时间：16-12-14 下午3:14
 * 修改备注：
 */
@LayoutInject(name = "FriendInfoLayout")
public class FriendInfoActivity extends BaseActivity<FriendInfoLayout>{

    public static void start(Context c,int key){
        start(c,FriendInfoActivity.class,key);
    }

    SchoolMate schoolMate;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_friend_info_layout);
        schoolMate = (SchoolMate) params;
        mLayout.bindSchoolMate(schoolMate);
    }

    @InterfaceInject(bindName = "onLayoutActionListener")
    FriendInfoLayout.OnLayoutActionListener onLayoutActionListener = new FriendInfoLayout.OnLayoutActionListener() {
        @Override
        public void onBack() {
            finish();
        }

        @Override
        public void addFriend() {
            doAddFriend();
        }
    };

    private void doAddFriend() {//// TODO: 17-2-14 need to upload schoolmate add 
        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .content(R.string.request_sending)
                .progress(true, 0)
                .progressIndeterminateStyle(false)
                .widgetColor(mAppCtx.getResources().getColor(R.color.hitalk_deep_yellow))
                .build();
        AVUser avUser = UserCacheHelper.getInstance().getCachedAVUser(schoolMate.getUserId());
        if (avUser != null) {
            dialog.show();
            Task.forResult(null).continueWithTask(task -> FriendsManager.getInstance().createAddRequestInBackground(avUser))
                    .continueWith(task -> {
                        Exception error = task.getError();
                        if (error != null){
                            NLog.i(TagUtil.makeTag(getClass()),"error = "+error.getMessage());
                            NToast.shortToast(mAppCtx,error.getMessage());
                        }else {
                            NToast.shortToast(mAppCtx,R.string.request_send);
                        }

                        dialog.dismiss();
                        mLayout.enableAddFriend();
                        return error == null;
                    }, Task.UI_THREAD_EXECUTOR);
        } else {
            mLayout.enableAddFriend();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

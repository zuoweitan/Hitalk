package com.vivifram.second.hitalk.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.afollestad.materialdialogs.MaterialDialog;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.base.BaseActivity;
import com.vivifram.second.hitalk.base.LayoutInject;
import com.vivifram.second.hitalk.bean.address.AddRequest;
import com.vivifram.second.hitalk.manager.chat.FriendsManager;
import com.vivifram.second.hitalk.state.DoneCallback;
import com.vivifram.second.hitalk.ui.layout.NewFriendConfirmLayout;
import com.vivifram.second.hitalk.ui.springview.widget.SpringView;
import com.zuowei.utils.bridge.EaterManager;
import com.zuowei.utils.bridge.params.address.NewFriendAdded;
import com.zuowei.utils.chat.ConversationUtils;
import com.zuowei.utils.common.SyncUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import bolts.Continuation;
import bolts.Task;

/**
 * 项目名称：Hitalk
 * 类描述：
 * 创建人：zuowei
 * 创建时间：16-11-1 下午2:13
 * 修改人：zuowei
 * 修改时间：16-11-1 下午2:13
 * 修改备注：
 */
@LayoutInject(name = "NewFriendConfirmLayout")
public class NewFriendConfirmActivity extends BaseActivity<NewFriendConfirmLayout>{

    public static void start(Context c){
        Intent intent = new Intent(c,NewFriendConfirmActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        c.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_new_friend_confirm_layout);

        loadMoreAddRequest(true,((list, e) -> {
            if (e == null) {
                mLayout.setData(list);
            }
        }));

        mLayout.setOnTitleActionListener(new NewFriendConfirmLayout.OnTitleActionListener() {
            @Override
            public void onBack() {
                finish();
            }
        });

        mLayout.setOnFreshListener(new SpringView.OnFreshListener() {
            @Override
            public void onRefresh() {
                loadMoreAddRequest(true, (list, e) -> {
                    if (e == null) {
                        mLayout.setData(list);
                    }
                    mLayout.notifyFreshDone();
                });
            }

            @Override
            public void onLoadmore() {
                loadMoreAddRequest(true, (list, e) -> {
                    if (e == null) {
                        mLayout.add(list);
                    }
                    mLayout.notifyFreshDone();
                });
            }
        });

        mLayout.setOnAcceptActionListener((addRequest, continuation) -> {
            accept(addRequest,continuation);
        });
    }

    private void accept(final AddRequest addRequest,Continuation<Boolean,Void> continuation) {
        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .content(R.string.trying)
                .progress(true, 0)
                .progressIndeterminateStyle(false)
                .widgetColor(mAppCtx.getResources().getColor(R.color.hitalk_deep_yellow))
                .build();
        Task.callInBackground(()->{
            int syncLatch = SyncUtils.getSyncLatch();
            AtomicBoolean success = new AtomicBoolean(false);
            FriendsManager.getInstance().agreeAddRequest(addRequest, new SaveCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null) {
                        success.set(true);
                        if (addRequest.getFromUser() != null) {
                            sendWelcomeMessage(addRequest.getFromUser().getObjectId());
                        }
                        EaterManager.getInstance().broadcast(new NewFriendAdded());
                    }
                    SyncUtils.notify(syncLatch);
                }
            });
            SyncUtils.wait(syncLatch);
            return success.get();
        }).continueWith(task -> {
            dialog.dismiss();
            return task.getResult();
        },Task.UI_THREAD_EXECUTOR).continueWith(continuation);
    }

    private void sendWelcomeMessage(String toUserId) {
        ConversationUtils.createSingleConversation(toUserId, new AVIMConversationCreatedCallback() {
            @Override
            public void done(AVIMConversation avimConversation, AVIMException e) {
                if (e == null) {
                    AVIMTextMessage message = new AVIMTextMessage();
                    message.setText(getString(R.string.message_when_agree_request));
                    avimConversation.sendMessage(message, null);
                }
            }
        });
    }

    private void loadMoreAddRequest(boolean isRefresh, DoneCallback<AddRequest> doneCallback) {
        FriendsManager.getInstance().findAddRequests(isRefresh ? 0 : mLayout.getCurrentSize(), 20, new FindCallback<AddRequest>() {
            @Override
            public void done(List<AddRequest> list, AVException e) {
                final List<AddRequest> filters = new ArrayList<>();
                if (list != null) {
                    FriendsManager.getInstance().markAddRequestsRead(list);
                    for (AddRequest addRequest : list) {
                        if (addRequest.getFromUser() != null) {
                            filters.add(addRequest);
                        }
                    }
                }
                if (doneCallback != null) {
                    doneCallback.done(filters,e);
                }
            }
        });
    }
}

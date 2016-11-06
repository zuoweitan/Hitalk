package com.vivifram.second.hitalk.ui.page;

import android.os.Handler;

import com.afollestad.materialdialogs.MaterialDialog;
import com.avos.avoscloud.AVUser;
import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.base.EatMark;
import com.vivifram.second.hitalk.base.LayoutInject;
import com.vivifram.second.hitalk.bean.address.SchoolMate;
import com.vivifram.second.hitalk.manager.chat.FriendsManager;
import com.vivifram.second.hitalk.manager.chat.SchoolMatesManager;
import com.vivifram.second.hitalk.ui.page.layout.AddressFragmentSub1Layout;
import com.vivifram.second.hitalk.ui.springview.widget.SpringView;
import com.zuowei.utils.bridge.constant.EaterAction;
import com.zuowei.utils.bridge.params.LightParam;
import com.zuowei.utils.bridge.params.address.AddressActionParam;
import com.zuowei.utils.common.NLog;
import com.zuowei.utils.common.NToast;
import com.zuowei.utils.common.TagUtil;
import com.zuowei.utils.handlers.AbstractHandler;
import com.zuowei.utils.helper.UserCacheHelper;
import com.zuowei.utils.pinyin.LetterComparator;

import java.util.Collections;
import java.util.List;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by zuowei on 16-10-11.
 */
@LayoutInject(name = "AddressFragmentSub1Layout")
public class AddressFragmentSub1 extends LazyFragment<AddressFragmentSub1Layout> {
    Handler handler;
    @Override
    protected void lazyLoad() {
    }

    @Override
    protected void onViewCreated() {
        super.onViewCreated();
        updateSchoolMates(task -> {
            if (task.getResult() != null) {
                mLayout.setData(task.getResult());
            }
            return null;
        });
        mLayout.setOnFreshListener(new SpringView.OnFreshListener() {
            @Override
            public void onRefresh() {
                updateSchoolMates(task -> {
                    mLayout.notifyFreshDone();
                    if (task.getResult() != null) {
                        mLayout.refresh(task.getResult());
                    }
                    return null;
                });
            }

            @Override
            public void onLoadmore() {

            }
        });
        handler = new Handler();

        mLayout.setOnSchoolMatesActionListener((userId,continueTask) -> {

            final MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                    .content(R.string.request_sending)
                    .progress(true, 0)
                    .progressIndeterminateStyle(false)
                    .widgetColor(mAppCtx.getResources().getColor(R.color.hitalk_deep_yellow))
                    .build();
            AVUser avUser = UserCacheHelper.getInstance().getCachedAVUser(userId);
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
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                            return error == null;
                        }, Task.UI_THREAD_EXECUTOR).continueWith(continueTask);
            }

        });
    }

    @EatMark(action = EaterAction.ACTION_ON_ADDRESS)
    public class NewFriendAddedListener extends AbstractHandler<AddressActionParam> {

        @Override
        public boolean isParamAvailable(LightParam param) {
            return param != null && param instanceof AddressActionParam;
        }

        @Override
        public void doJobWithParam(AddressActionParam param) {
            if (param.getActionType() == AddressActionParam.ACTION_NEW_FRIEND_ADDED){
            }
        }
    }

    private void updateSchoolMates(Continuation<List<SchoolMate>,Void> continuation) {
        SchoolMatesManager.getInstance().queryAllSchoolMates()
                .continueWith(task -> {
                    final List<SchoolMate> result = task.getResult();
                    if (result != null){
                        Collections.sort(result,new LetterComparator<>());
                        if (continuation != null) {
                            Task.<List<SchoolMate>>forResult(result)
                                    .continueWith(continuation,Task.UI_THREAD_EXECUTOR);
                        }
                    }else {
                        if (continuation != null) {
                            Task.<List<SchoolMate>>forResult(null)
                                    .continueWith(continuation,Task.UI_THREAD_EXECUTOR);
                        }
                    }

                    return null;
                });
    }

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_address_sub_1_layout;
    }
}

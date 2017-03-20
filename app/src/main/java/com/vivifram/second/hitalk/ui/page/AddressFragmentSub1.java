package com.vivifram.second.hitalk.ui.page;

import android.os.Handler;

import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.base.EatMark;
import com.vivifram.second.hitalk.base.LayoutInject;
import com.vivifram.second.hitalk.bean.Constants;
import com.vivifram.second.hitalk.bean.address.SchoolMate;
import com.vivifram.second.hitalk.manager.chat.FriendsManager;
import com.vivifram.second.hitalk.manager.chat.SchoolMatesManager;
import com.vivifram.second.hitalk.ui.ChatRoomActivity;
import com.vivifram.second.hitalk.ui.page.layout.AddressFragmentSub1Layout;
import com.vivifram.second.hitalk.ui.recycleview.address.SchoolMatesAdapter;
import com.vivifram.second.hitalk.ui.springview.widget.SpringView;
import com.zuowei.utils.bridge.constant.EaterAction;
import com.zuowei.utils.bridge.params.LightParam;
import com.zuowei.utils.bridge.params.address.AddressActionParam;
import com.zuowei.utils.common.Md5Utils;
import com.zuowei.utils.common.NLog;
import com.zuowei.utils.common.TagUtil;
import com.zuowei.utils.handlers.AbstractHandler;
import com.zuowei.utils.helper.HiTalkHelper;
import com.zuowei.utils.helper.SchoolmatesCacheHelper;
import com.zuowei.utils.pinyin.LetterComparator;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public void onClientOpen() {
        super.onClientOpen();
        /*if (mLayout != null) {
            updateSchoolMates(task -> {
                if (task.getResult() != null) {
                    mLayout.setData(task.getResult());
                }
                return null;
            });
        }*/
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

        mLayout.setOnSchoolMatesActionListener(new SchoolMatesAdapter.OnSchoolMatesActionListener() {
            @Override
            public void onAddFriendRequest(String userId, Continuation<Boolean, Void> callback) {
                FriendsManager.FriendsManagerUIHelper.requestFriend(userId, callback);
            }

            @Override
            public void onSchoolMateItemClick(String userId, int state) {
                NLog.i(TagUtil.makeTag(AddressFragmentSub1.class), "onSchoolMateItemClick userId = "+ userId + ", state = "+ state);
                if (state == SchoolmatesCacheHelper.REQUEST_STATE_FAILED) {
                    ChatRoomActivity.start(getActivity(), Constants.ParamsKey.Chat.TO_SCHOOL_MATE, userId);
                } else {
                    ChatRoomActivity.start(getActivity(), Constants.ParamsKey.Chat.TO_FRIEND, userId);
                }
            }
        });
    }

    @EatMark(action = EaterAction.ACTION_ON_ADDRESS)
    public class SchoolStateChangedListener extends AbstractHandler<AddressActionParam> {

        @Override
        public boolean isParamAvailable(LightParam param) {
            return param != null && param instanceof AddressActionParam;
        }

        @Override
        public void doJobWithParam(AddressActionParam param) {
            if (param.getActionType() == AddressActionParam.ACTION_SCHOOLMATE_STATE_CHANGED){
                NLog.i(TagUtil.makeTag(AddressFragmentSub1.class), "receive SchoolStateChangedListener " + param.getActionType());
                mLayout.refresh();
            }
        }
    }

    private void updateSchoolMates(Continuation<List<SchoolMate>,Void> continuation) {
        Map<String,Object> conditions = new HashMap<>();
        conditions.put(Constants.User.COLLEGECODE_C
                , Md5Utils.stringToMD5(HiTalkHelper.$().getCurrentUserCollege()));
        SchoolMatesManager.getInstance().queryAllSchoolMatesWithState(conditions)
                .continueWith(task -> {
                    final List<SchoolMate> result = task.getResult();
                    if (result != null){
                        Collections.sort(result,new LetterComparator<>());
                        if (continuation != null) {
                            Task.forResult(result)
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

package com.vivifram.second.hitalk.ui.page;

import com.avos.avoscloud.AVCallback;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.CountCallback;
import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.base.EatMark;
import com.vivifram.second.hitalk.base.LayoutInject;
import com.vivifram.second.hitalk.bean.address.Friend;
import com.vivifram.second.hitalk.manager.chat.FriendsManager;
import com.vivifram.second.hitalk.state.DoneCallback;
import com.vivifram.second.hitalk.ui.page.layout.AddressFragmentSub2Layout;
import com.vivifram.second.hitalk.ui.springview.widget.SpringView;
import com.vivifram.second.hitalk.ui.view.CommonItem;
import com.zuowei.dao.greendao.User;
import com.zuowei.utils.bridge.EaterManager;
import com.zuowei.utils.bridge.constant.EaterAction;
import com.zuowei.utils.bridge.params.LightParam;
import com.zuowei.utils.bridge.params.address.AddressActionParam;
import com.zuowei.utils.bridge.params.address.SchoolMateStateParam;
import com.zuowei.utils.bridge.params.address.UnReadRequestCountParam;
import com.zuowei.utils.bridge.params.push.InvitationParam;
import com.zuowei.utils.handlers.AbstractHandler;
import com.zuowei.utils.helper.SchoolmatesCacheHelper;
import com.zuowei.utils.pinyin.CharacterParser;
import com.zuowei.utils.pinyin.LetterComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by zuowei on 16-10-11.
 */
@LayoutInject(name = "AddressFragmentSub2Layout")
public class AddressFragmentSub2 extends LazyFragment<AddressFragmentSub2Layout> {
    @Override
    protected void lazyLoad() {
    }

    @Override
    protected void onViewCreated() {
        super.onViewCreated();
        refresh();
        fetchFriends(true,null);
        mLayout.setOnFreshListener(new SpringView.OnFreshListener() {
            @Override
            public void onRefresh() {
                fetchFriends(false,((list, e) -> {
                    mLayout.notifyFreshDone();
                }));
            }

            @Override
            public void onLoadmore() {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        updateNewRequestBadge();
    }

    private void fetchFriends(final boolean isforce, DoneCallback<Friend> doneCallback) {
        FriendsManager.getInstance().fetchFriends(isforce, new AVCallback<List<User>>() {
            @Override
            protected void internalDone0(List<User> users, AVException e) {

                ArrayList<Friend> friends = new ArrayList<>();
                if (e == null && users != null){
                    for (User user : users) {
                        Friend friend = convertToFriend(user);
                        if (friend != null){
                            friends.add(friend);
                        }
                    }
                    if (friends.size() > 0) {
                        Collections.sort(friends, new LetterComparator<>());
                        mLayout.refresh(friends);
                    }
                }

                if (doneCallback != null) {
                    doneCallback.done(friends,e);
                }
            }
        });
    }

    private Friend convertToFriend(User user) {
        if (user != null) {
            Friend friend = new Friend();
            friend.setNickName(user.getNick());
            friend.setAvatarUrl(user.getAvatar());
            friend.setUserId(user.getObjectId());
            String pinyin = CharacterParser.getInstance().getSelling(friend.getNickName());
            String sortString = pinyin.substring(0, 1).toUpperCase();

            if (sortString.matches("[A-Z]")) {
                friend.setSortLetters(sortString.toUpperCase());
            } else {
                friend.setSortLetters("#");
            }
            return friend;
        }
        return null;
    }

    @EatMark(action = EaterAction.ACTION_ON_ADDRESS)
    public class NewFriendAddedListener extends AbstractHandler<AddressActionParam>{

        @Override
        public boolean isParamAvailable(LightParam param) {
            return param != null && param instanceof AddressActionParam;
        }

        @Override
        public void doJobWithParam(AddressActionParam param) {
            if (param.getActionType() == AddressActionParam.ACTION_NEW_FRIEND_ADDED){
                fetchFriends(true, (list, e) -> {
                    if (list != null) {
                        for (Friend friend : list) {
                            SchoolmatesCacheHelper.getInstance().update(friend.getUserId(),SchoolmatesCacheHelper.REQUEST_STATE_SUCCESS);
                        }
                        EaterManager.getInstance().broadcast(new SchoolMateStateParam());
                    }
                });
            }
        }
    }

    @EatMark(action = EaterAction.ACTION_DO_INVITATION)
    public class InvitateListener extends AbstractHandler<InvitationParam> {

        @Override
        public boolean isParamAvailable(LightParam param) {
            return param != null && param instanceof InvitationParam;
        }

        @Override
        public void doJobWithParam(InvitationParam param) {
            FriendsManager.getInstance().unreadRequestsIncrement();
            updateNewRequestBadge();
        }
    }

    private void updateNewRequestBadge() {
        CommonItem newfCi = mLayout.getNewfCi();
        newfCi.showBadge(FriendsManager.getInstance().hasUnreadRequests());
    }

    private void refresh() {
        FriendsManager.getInstance().countUnreadRequests(new CountCallback() {
            @Override
            public void done(int i, AVException e) {
                EaterManager.getInstance().broadcast(new UnReadRequestCountParam().setUnReadCount(i));
            }
        });
    }

    @EatMark(action = EaterAction.ACTION_ON_ADDRESS)
    public class RequestCountUpdate extends AbstractHandler<UnReadRequestCountParam>{

        @Override
        public boolean isParamAvailable(LightParam param) {
            return param != null && param instanceof UnReadRequestCountParam;
        }

        @Override
        public void doJobWithParam(UnReadRequestCountParam param) {
            updateNewRequestBadge();
        }
    }

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_address_sub_2_layout;
    }
}

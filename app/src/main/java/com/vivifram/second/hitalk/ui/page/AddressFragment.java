package com.vivifram.second.hitalk.ui.page;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.base.EatMark;
import com.vivifram.second.hitalk.base.LayoutInject;
import com.vivifram.second.hitalk.manager.chat.FriendsManager;
import com.vivifram.second.hitalk.ui.page.layout.AddressFragmentLayout;
import com.zuowei.utils.bridge.constant.EaterAction;
import com.zuowei.utils.bridge.params.LightParam;
import com.zuowei.utils.bridge.params.address.UnReadRequestCountParam;
import com.zuowei.utils.bridge.params.push.InvitationParam;
import com.zuowei.utils.common.NLog;
import com.zuowei.utils.common.TagUtil;
import com.zuowei.utils.handlers.AbstractHandler;

/**
 * Created by zuowei on 16-7-25.
 */
@LayoutInject(name = "AddressFragmentLayout")
public class AddressFragment extends LazyFragment<AddressFragmentLayout> {

    private Fragment[] mFragments;
    private AddressFragmentSub1 addressFragmentSub1;
    private AddressFragmentSub2 addressFragmentSub2;
    private AddressFragmentSub3 addressFragmentSub3;
    private int mIndex;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        addressFragmentSub1 = new AddressFragmentSub1();
        addressFragmentSub2 = new AddressFragmentSub2();
        addressFragmentSub3 = new AddressFragmentSub3();

        mFragments = new Fragment[]{addressFragmentSub1,addressFragmentSub2,addressFragmentSub3};
        
        //// TODO: 17-2-14 try to update the schoolmate friendstate.{AddressFragmentsub2} 
    }

    @Override
    protected void onViewCreated() {
        super.onViewCreated();
        showContent();
        init();
    }

    private void showContent() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .add(mLayout.getContentId(), addressFragmentSub1)
                .add(mLayout.getContentId(), addressFragmentSub2)
                .add(mLayout.getContentId(), addressFragmentSub3)
                .hide(addressFragmentSub2)
                .hide(addressFragmentSub3)
                .show(addressFragmentSub1).commit();

        mIndex = 0;
    }

    private void init() {
        mLayout.setOnTabClickListener(new AddressFragmentLayout.OnTabClickListener() {
            @Override
            public void onTabClick(int index) {
                switchTab(index);
            }
        });
    }

    private void switchTab(int index) {
        if (index != mIndex) {
            FragmentTransaction trx = getActivity().getSupportFragmentManager()
                    .beginTransaction();
            trx.hide(mFragments[mIndex]);
            if (!mFragments[index].isAdded()) {
                trx.add(mLayout.getContentId(), mFragments[index]);
            }
            trx.show(mFragments[index]).commit();
        }

        mIndex = index;
    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_address_layout;
    }

    @EatMark(action = EaterAction.ACTION_DO_INVITATION)
    public class InvitateListener extends AbstractHandler<InvitationParam> {

        @Override
        public boolean isParamAvailable(LightParam param) {
            return param != null && param instanceof InvitationParam;
        }

        @Override
        public void doJobWithParam(InvitationParam param) {
            NLog.i(TagUtil.makeTag(getClass()),"InvitateListener received");
            FriendsManager.getInstance().unreadRequestsIncrement();
            updateNewRequestBadge();
        }
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

    private void updateNewRequestBadge() {
        AddressFragmentLayout.Tab tab = mLayout.get(2);
        tab.showBadge(FriendsManager.getInstance().hasUnreadRequests());
    }

    @Override
    public void onResume() {
        super.onResume();
        updateNewRequestBadge();
    }
}

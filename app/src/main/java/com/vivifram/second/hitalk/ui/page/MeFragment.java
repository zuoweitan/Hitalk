package com.vivifram.second.hitalk.ui.page;

import com.avos.avoscloud.AVUser;
import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.base.LayoutInject;
import com.vivifram.second.hitalk.ui.SettingActivity;
import com.vivifram.second.hitalk.ui.page.layout.MeFragmentLayout;
import com.zuowei.utils.helper.HiTalkHelper;
import com.zuowei.utils.helper.UserCacheHelper;

/**
 * Created by zuowei on 16-7-25.
 */
@LayoutInject(name = "MeFragmentLayout")
public class MeFragment extends LazyFragment<MeFragmentLayout> {
    @Override
    protected void lazyLoad() {
        requestUserData();
    }

    @Override
    protected void onViewCreated() {
        super.onViewCreated();
        mLayout.setOnItemClickListener(new MeFragmentLayout.OnItemClickListener() {
            @Override
            public void onSettingItemClick() {
                SettingActivity.start(mAppCtx);
            }
        });
    }

    private void requestUserData() {
        AVUser avUser = HiTalkHelper.getInstance().getCurrentUser();
        mLayout.setAvatar(UserCacheHelper.getInstance().getAvatarUrl(avUser));
        if (avUser.has("nickName")){
            mLayout.setNick((String) avUser.get("nickName"));
        }else {
            mLayout.setNick("");
        }

        if (avUser.has("signature")){
            mLayout.setSignature((String) avUser.get("signature"));
        }else {
            mLayout.setSignature("");
        }
    }

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_me_layout;
    }
}

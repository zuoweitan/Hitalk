package com.vivifram.second.hitalk.ui.page;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.base.LayoutInject;
import com.vivifram.second.hitalk.bean.IMessageWrap;
import com.vivifram.second.hitalk.ui.page.layout.HitalkFragmentLayout;
import com.vivifram.second.hitalk.ui.page.layout.HitalkFragmentSub2Layout;
import com.zuowei.utils.bridge.params.LightParam;
import com.zuowei.utils.bridge.params.chat.MessageParam;
import com.zuowei.utils.handlers.AbstractHandler;

/**
 * Created by zuowei on 16-7-25.
 */
@LayoutInject(name = "HitalkFragmentSub2Layout")
public class HiTalkFragmentSub2 extends LazyFragment<HitalkFragmentSub2Layout> {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_hi_sub_2_layout;
    }
}

package com.vivifram.second.hitalk.ui.layout;

import android.view.View;

import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.ui.view.BGATitlebar;

/**
 * 项目名称：Hitalk
 * 类描述：
 * 创建人：zuowei
 * 创建时间：17-2-20 上午10:51
 * 修改人：zuowei
 * 修改时间：17-2-20 上午10:51
 * 修改备注：
 */
public class ChatRoomLayout extends BaseLayout{

    public ChatRoomLayout(View rootView) {
        super(rootView);
    }

    private BGATitlebar titlebar;

    @Override
    public void onContentViewCreate(View view) {
        super.onContentViewCreate(view);
        titlebar = (BGATitlebar) findViewById(R.id.titleBar);

    }

    public void setTitle(String title){
        titlebar.setTitleText(title);
    }
}

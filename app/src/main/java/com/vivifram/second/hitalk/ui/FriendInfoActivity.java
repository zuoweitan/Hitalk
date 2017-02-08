package com.vivifram.second.hitalk.ui;

import android.content.Context;
import android.os.Bundle;

import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.base.BaseActivity;
import com.vivifram.second.hitalk.base.LayoutInject;
import com.vivifram.second.hitalk.bean.address.SchoolMate;
import com.vivifram.second.hitalk.ui.layout.FriendInfoLayout;

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

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_friend_info_layout);
        SchoolMate schoolMate = (SchoolMate) params;
    }
}

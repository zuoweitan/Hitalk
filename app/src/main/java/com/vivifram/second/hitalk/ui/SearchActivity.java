package com.vivifram.second.hitalk.ui;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.base.BaseActivity;
import com.vivifram.second.hitalk.base.LayoutInject;
import com.vivifram.second.hitalk.ui.layout.SearchLayout;

/**
 * 项目名称：Hitalk
 * 类描述：
 * 创建人：zuowei
 * 创建时间：16-12-14 下午8:45
 * 修改人：zuowei
 * 修改时间：16-12-14 下午8:45
 * 修改备注：
 */
@LayoutInject(name = "SearchLayout")
public class SearchActivity extends BaseActivity<SearchLayout>{

    public static void start(Context c){
        start(c,SearchActivity.class);
    }

    public static void startWithAnimation(Activity activity, View shared){
        Intent intent = new Intent(activity,SearchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent
                ,ActivityOptions.makeSceneTransitionAnimation(activity,shared,"sharedName").toBundle());
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_search_layout);

    }
}

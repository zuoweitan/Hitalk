package com.vivifram.second.hitalk.ui.layout;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.vivifram.second.hitalk.R;

/**
 * 项目名称：Hitalk
 * 类描述：
 * 创建人：zuowei
 * 创建时间：16-12-14 下午8:46
 * 修改人：zuowei
 * 修改时间：16-12-14 下午8:46
 * 修改备注：
 */
public class SearchLayout extends BaseLayout{

    public SearchLayout(View rootView) {
        super(rootView);
    }

    @Override
    public void onContentViewCreate(View view) {
        super.onContentViewCreate(view);

    }

    public void bind(FragmentManager fragmentManager, Fragment fragment){
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment).commit();
    }
}

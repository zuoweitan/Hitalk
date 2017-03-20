package com.vivifram.second.hitalk.base;

import android.view.View;

/**
 * 项目名称：Hitalk
 * 类描述：
 * 创建人：zuowei
 * 创建时间：17-3-20 下午4:13
 * 修改人：zuowei
 * 修改时间：17-3-20 下午4:13
 * 修改备注：
 */
public class BaseLayoutBean implements View.OnClickListener {

    protected View base;
    public BaseLayoutBean (View base){
        this.base = base;
        LayoutIdBinder.LAYOUT_BEAN.bindView(this);
    }

    @Override
    public void onClick(View v) {

    }

    public View findViewById(int id){
        return base.findViewById(id);
    }
}

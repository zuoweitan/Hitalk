package com.vivifram.second.hitalk.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.vivifram.second.hitalk.bean.Constants;
import com.zuowei.utils.bridge.EaterManager;
import com.zuowei.utils.bridge.params.ViewHolderOnclickParam;

import static com.vivifram.second.hitalk.base.LayoutIdBinder.LAYOUT_RECYCLER_VIEWHOLDER;

/**
 * 项目名称：Hitalk
 * 类描述：
 * 创建人：zuowei
 * 创建时间：17-3-23 下午5:29
 * 修改人：zuowei
 * 修改时间：17-3-23 下午5:29
 * 修改备注：
 */
public class BaseRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public BaseRecyclerViewHolder(View itemView) {
        super(itemView);
        LAYOUT_RECYCLER_VIEWHOLDER.bindViewToHolder(this);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == itemView) {
            EaterManager.getInstance().broadcast(new ViewHolderOnclickParam()
                    .setPosition(getAdapterPosition())
                    .setType(getViewHolderType()));
        }
    }

    protected int getViewHolderType() {
        return Constants.ViewHolderType.VIEWHOLDER_BASE_TYPE;
    }

    public View findViewById(int id){
        return itemView.findViewById(id);
    }
}

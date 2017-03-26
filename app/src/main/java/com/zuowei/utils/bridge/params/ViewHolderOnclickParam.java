package com.zuowei.utils.bridge.params;

import com.zuowei.utils.bridge.constant.EaterAction;

/**
 * Created by zuowei on 17-3-26.
 */

public class ViewHolderOnclickParam extends LightParam {
    int position;
    int type;

    public ViewHolderOnclickParam() {
        super(EaterAction.ACTION_VIEWHOLDER_ONCLICK);
    }

    public ViewHolderOnclickParam setPosition(int position) {
        this.position = position;
        return this;
    }

    public ViewHolderOnclickParam setType(int type) {
        this.type = type;
        return this;
    }

    public int getPosition() {
        return position;
    }

    public int getType() {
        return type;
    }
}

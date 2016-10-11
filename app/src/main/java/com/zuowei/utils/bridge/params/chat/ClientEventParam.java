package com.zuowei.utils.bridge.params.chat;

import com.zuowei.utils.bridge.constant.EaterAction;
import com.zuowei.utils.bridge.params.LightParam;

/**
 * Created by zuowei on 16-8-5.
 */
public abstract class ClientEventParam extends LightParam {
    public static final int ACTION_CONNECT_CHANGED = 0x01;
    public static final int ACTION_OPEN_CLIENT = 0x02;
    public ClientEventParam() {
        super(EaterAction.ACTION_DO_CHECK_CLIENT);
    }

    public abstract int getActionType();
}

package com.zuowei.utils.handlers;

import com.zuowei.utils.bridge.params.LightParam;
import com.zuowei.utils.bridge.params.ParamWrap;
import com.zuowei.utils.bridge.params.chat.ClientOpenParam;
import com.zuowei.utils.bridge.params.chat.MessageParam;

/**
 * Created by zuowei on 17-3-19.
 */

public abstract class ClientOpenHandler extends AbstractHandler<ClientOpenParam> {

    @Override
    public boolean isParamAvailable(LightParam param) {
        return param != null && param instanceof ClientOpenParam;
    }

    @Override
    public abstract void doJobWithParam(ParamWrap<ClientOpenParam> paramWrap);
}

package com.zuowei.utils.handlers;

import com.zuowei.utils.bridge.IEater;
import com.zuowei.utils.bridge.params.LightParam;

/**
 * Created by zuowei on 16-7-18.
 */
public abstract class AbstractHandler<T extends LightParam> implements IEater {

    @Override
    public final void onEat(LightParam param) {
        if (isParamAvailable(param)){
            doJobWithParam((T) param);
        }
    }

    public abstract boolean isParamAvailable(LightParam param);

    public abstract void doJobWithParam(T param);
}

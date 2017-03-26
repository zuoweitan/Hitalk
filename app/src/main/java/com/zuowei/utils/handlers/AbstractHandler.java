package com.zuowei.utils.handlers;

import com.zuowei.utils.bridge.IEater;
import com.zuowei.utils.bridge.params.LightParam;
import com.zuowei.utils.common.NLog;
import com.zuowei.utils.common.TagUtil;

/**
 * Created by zuowei on 16-7-18.
 */

public abstract class AbstractHandler<T extends LightParam> implements IEater {

    Class<T> target;

    @Override
    public final void onEat(LightParam param) {
        if (isParamAvailable(param)){
            doJobWithParam((T) param);
        }
    }

    public boolean isParamAvailable(LightParam param) {
        NLog.i(TagUtil.makeTag(AbstractHandler.class), "target = " + target);
        return target != null && param != null && target.getName().equals(param.getClass().getName());
    }

    public abstract void doJobWithParam(T param);
}

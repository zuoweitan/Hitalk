package com.zuowei.utils.handlers;

import com.zuowei.utils.bridge.IEater;
import com.zuowei.utils.bridge.params.LightParam;
import com.zuowei.utils.bridge.params.ParamWrap;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by zuowei on 16-7-18.
 */

public abstract class AbstractHandler<T extends LightParam> implements IEater {

    @Override
    public final void onEat(LightParam param) {
        if (isParamAvailable(param)){
            doJobWithParam(new ParamWrap<T>().wrap((T) param));
        }
    }

    public boolean isParamAvailable(LightParam param) {
        if (param != null) {
            Method method;
            try {
                method = getClass().getMethod("doJobWithParam", ParamWrap.class);
                Type[] genericParameterTypes = method.getGenericParameterTypes();
                Type type = genericParameterTypes[0];
                if (type instanceof ParameterizedType) {
                    Class c = (Class) ((ParameterizedType) type).getActualTypeArguments()[0];
                    return c.isInstance(param);
                }
                return false;
            } catch (NoSuchMethodException e) {
                return false;
            }
        }
        return false;
    }

    public abstract void doJobWithParam(ParamWrap<T> paramWrap);
}

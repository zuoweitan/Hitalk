package com.zuowei.utils.bridge.params;

/**
 * 项目名称：AazenWearHome
 * 类描述：
 * 创建人：zuowei
 * 创建时间：17-4-21 下午5:18
 * 修改人：zuowei
 * 修改时间：17-4-21 下午5:18
 * 修改备注：
 */
public class ParamWrap<T extends LightParam> {

    private T param;

    public ParamWrap wrap(T param) {
        this.param = param;
        return this;
    }

    public T getParam() {
        return param;
    }
}

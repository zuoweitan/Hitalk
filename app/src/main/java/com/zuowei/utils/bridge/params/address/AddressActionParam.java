package com.zuowei.utils.bridge.params.address;

import com.zuowei.utils.bridge.constant.EaterAction;
import com.zuowei.utils.bridge.params.LightParam;

/**
 * 项目名称：Hitalk
 * 类描述：
 * 创建人：zuowei
 * 创建时间：16-11-1 下午6:03
 * 修改人：zuowei
 * 修改时间：16-11-1 下午6:03
 * 修改备注：
 */
public abstract class AddressActionParam extends LightParam{

    public static final int ACTION_NEW_FRIEND_ADDED = 0x01;

    public AddressActionParam() {
        super(EaterAction.ACTION_ON_ADDRESS);
    }

    public abstract int getActionType();
}

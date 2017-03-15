package com.zuowei.utils.bridge.params.push;

import com.zuowei.utils.bridge.constant.EaterAction;
import com.zuowei.utils.bridge.params.LightParam;

/**
 * 项目名称：Hitalk
 * 类描述：
 * 创建人：zuowei
 * 创建时间：16-10-31 下午9:38
 * 修改人：zuowei
 * 修改时间：16-10-31 下午9:38
 * 修改备注：
 */
public class InvitationParam extends LightParam{

    public boolean justRefresh;

    public InvitationParam() {
        super(EaterAction.ACTION_DO_INVITATION);
    }

    public InvitationParam setJustRefresh(boolean justRefresh) {
        this.justRefresh = justRefresh;
        return this;
    }
}

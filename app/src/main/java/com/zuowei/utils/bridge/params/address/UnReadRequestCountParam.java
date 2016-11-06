package com.zuowei.utils.bridge.params.address;

/**
 * Created by zuowei on 16-11-6.
 */

public class UnReadRequestCountParam extends AddressActionParam{

    public int unReadCount;
    @Override
    public int getActionType() {
        return ACTION_UNREAD_REQUEST_COUNTS;
    }

    public UnReadRequestCountParam setUnReadCount(int unReadCount) {
        this.unReadCount = unReadCount;
        return this;
    }
}

package com.zuowei.utils.bridge.params.chat;

/**
 * Created by zuowei on 16-8-5.
 */
public class ConnectChangedParam extends ClientEventParam {
    public boolean mConnected;
    @Override
    public int getActionType() {
        return ACTION_CONNECT_CHANGED;
    }

    public ConnectChangedParam(boolean mConnected) {
        this.mConnected = mConnected;
    }
}

package com.zuowei.utils.bridge.params.chat;

/**
 * Created by zuowei on 16-8-5.
 */
public class ClientOpenParam extends ClientEventParam {
    public boolean mOpened;
    @Override
    public int getActionType() {
        return ACTION_OPEN_CLIENT;
    }

    public ClientOpenParam(boolean mOpened) {
        this.mOpened = mOpened;
    }
}

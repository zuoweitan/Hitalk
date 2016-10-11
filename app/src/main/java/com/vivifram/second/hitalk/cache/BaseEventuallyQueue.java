package com.vivifram.second.hitalk.cache;

import com.vivifram.second.hitalk.bean.BaseBean;
import com.zuowei.dao.greendao.Bean;

import bolts.Task;

/**
 * Created by zuowei on 16-8-25.
 */
public abstract class BaseEventuallyQueue {

    private boolean isConnected;

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public abstract void onDestroy();

    public abstract void pause();

    public abstract void resume();

    public abstract Task<Bean> enqueueEventuallyAsync(final BaseBean bean);
}

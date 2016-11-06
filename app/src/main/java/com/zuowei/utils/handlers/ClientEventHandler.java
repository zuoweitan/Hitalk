package com.zuowei.utils.handlers;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMClientEventHandler;
import com.zuowei.utils.bridge.EaterManager;
import com.zuowei.utils.bridge.params.chat.ConnectChangedParam;
import com.zuowei.utils.common.NLog;
import com.zuowei.utils.common.TagUtil;

/**
 * Created by zuowei on 16-8-4.
 */
public class ClientEventHandler extends AVIMClientEventHandler {
    private static ClientEventHandler sInstance;
    private volatile boolean connect = false;

    public static synchronized ClientEventHandler getInstance() {
        if(null == sInstance) {
            synchronized (ClientEventHandler.class) {
                if (null == sInstance) {
                    sInstance = new ClientEventHandler();
                }
            }
        }

        return sInstance;
    }

    private ClientEventHandler() {
    }

    public boolean isConnect() {
        return this.connect;
    }

    public void setConnectAndNotify(boolean isConnect) {
        this.connect = isConnect;
        NLog.i(TagUtil.makeTag(getClass()),"setConnectAndNotify isConnect = "+isConnect);
        EaterManager.getInstance().broadcast(new ConnectChangedParam(isConnect));
    }

    public void onConnectionPaused(AVIMClient avimClient) {
        this.setConnectAndNotify(false);
    }

    public void onConnectionResume(AVIMClient avimClient) {
        this.setConnectAndNotify(true);
    }

    public void onClientOffline(AVIMClient avimClient, int i) {
    }
}

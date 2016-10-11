package com.vivifram.second.hitalk.manager.chat;

import android.text.TextUtils;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.zuowei.utils.bridge.EaterManager;
import com.zuowei.utils.bridge.params.chat.ClientOpenParam;
import com.zuowei.utils.common.NLog;
import com.zuowei.utils.common.TagUtil;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by zuowei on 16-8-5.x
 */
public class ClientManager {

    private AVIMClient mAvimClient;
    private String mClientId;
    private boolean mOpened;
    private static  ClientManager sInstance;
    private Queue<OpenCmd> mCmdQueue;
    public static ClientManager getInstance(){
        if (sInstance == null) {
            synchronized (ClientManager.class){
                if (sInstance == null) {
                    sInstance = new ClientManager();
                }
            }
        }

        return sInstance;
    }

    private ClientManager(){
        mCmdQueue = new ConcurrentLinkedQueue<>();
    }

    public synchronized void open(String clientId, final AVIMClientCallback callback) {
        NLog.i(TagUtil.makeTag(ClientManager.class),"open client");
        OpenCmd openCmd = new OpenCmd();
        openCmd.clientId = clientId;
        openCmd.callback = callback;
        mClientId = clientId;
        mAvimClient = AVIMClient.getInstance(clientId);
        mCmdQueue.add(openCmd);
        if (mCmdQueue.size() == 1){
            doOpen(clientId, callback);
        }
    }

    private void doOpen(String clientId, final AVIMClientCallback callback) {
        mClientId = clientId;
        mAvimClient = AVIMClient.getInstance(clientId);
        mAvimClient.open(new AVIMClientCallback() {
            @Override
            public void done(AVIMClient avimClient, AVIMException e) {
                NLog.i(TagUtil.makeTag(ClientManager.class),"e = "+(e == null?"success":e.getMessage()));
                if (e == null){
                    mCmdQueue.clear();
                    mOpened = true;
                }else {
                    if (mCmdQueue.size() > 0) {
                        OpenCmd openCmd = mCmdQueue.poll();
                        doOpen(openCmd.clientId, openCmd.callback);

                    }
                }
                EaterManager.getInstance().broadcast(new ClientOpenParam(isOpend()));
                if (callback != null) {
                    callback.done(avimClient, e);
                }
            }
        });
    }

    public void close(final AVIMClientCallback callback) {
        AVIMClient.getInstance(mClientId).close(new AVIMClientCallback() {
            public void done(AVIMClient avimClient, AVIMException e) {
                mOpened = false;
                mClientId = null;
                if(null != callback) {
                    callback.internalDone(avimClient, e);
                }

            }
        });
    }

    public AVIMClient getClient() {
        return mAvimClient;
    }

    public synchronized boolean isOpend(){
        return mOpened;
    }

    public String getClientId() {
        if (TextUtils.isEmpty(mClientId)) {
            throw new IllegalStateException("Please call AVImClientManager.open first");
        }
        return mClientId;
    }

    class OpenCmd{
        String clientId;
        AVIMClientCallback callback;
    }
}
package com.zuowei.utils.handlers;

import com.zuowei.utils.bridge.EaterManager;
import com.zuowei.utils.bridge.constant.EaterAction;
import com.zuowei.utils.bridge.params.LogoutParam;
import com.zuowei.utils.bridge.params.ParamWrap;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by zuowei on 16-7-18.
 */
public class LogoutHandler extends AbstractHandler<LogoutParam>{

    private static LogoutHandler sInstance;

    public static LogoutHandler getInstance(){
        if (sInstance == null){
            synchronized (LogoutHandler.class){
                if (sInstance == null){
                    sInstance = new LogoutHandler();
                }
            }
        }
        return sInstance;
    }

    private LogoutHandler() {
        EaterManager.getInstance().registerEater(EaterAction.ACTION_DO_LOGOUT, this);
        onLogoutListeners = new CopyOnWriteArrayList<>();
    }

    public interface OnLogoutListener {
        void logout();
    }

    private CopyOnWriteArrayList<OnLogoutListener> onLogoutListeners;

    @Override
    public void doJobWithParam(ParamWrap<LogoutParam> paramWrap) {
        for (OnLogoutListener onLogoutListener : onLogoutListeners) {
            onLogoutListener.logout();
        }
    }

    public void addListener(OnLogoutListener onLogoutListener) {
        if (!onLogoutListeners.contains(onLogoutListener)) {
            onLogoutListeners.add(onLogoutListener);
        }
    }

    public void removeListener(OnLogoutListener onLogoutListener) {
        if (onLogoutListeners.contains(onLogoutListener)) {
            onLogoutListeners.remove(onLogoutListener);
        }
    }
}

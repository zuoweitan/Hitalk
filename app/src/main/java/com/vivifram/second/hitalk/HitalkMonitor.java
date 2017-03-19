package com.vivifram.second.hitalk;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

/**
 * Created by zuowei on 17-3-19.
 */

public class HitalkMonitor extends HandlerThread{

    public static final int LOOP_TIME = 5000;
    public static final int LOOP_IMMEDIATE = 500;

    public static final int BASE_MSG_TYPE = 0x00;
    public static final int CHECK_HITALK_CONVERSATION = BASE_MSG_TYPE + 1;

    private Handler handler;
    private static HitalkMonitor hitalkMonitor;

    public static void runLoop(){
        if (hitalkMonitor == null){
            synchronized (HitalkMonitor.class) {
                if (hitalkMonitor == null) {
                    hitalkMonitor = new HitalkMonitor("HitalkMonitor");
                    hitalkMonitor.start();
                    hitalkMonitor.init();
                }
            }
        }
    }

    private HitalkMonitor(String name) {
        super(name);
    }

    private HitalkMonitor(String name, int priority) {
        super(name, priority);
    }

    private void init() {
        handler = new Handler(getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case CHECK_HITALK_CONVERSATION:
                            MonitorEvent monitorEvent = (MonitorEvent) msg.obj;
                            Message newMsg = new Message();
                            newMsg.copyFrom(msg);
                            if (!monitorEvent.check()) {
                                monitorEvent.run();
                                handler.sendMessageDelayed(newMsg,
                                        LOOP_IMMEDIATE);
                            } else {
                                handler.sendMessageDelayed(newMsg,
                                        LOOP_TIME);
                            }
                        break;
                }
            }
        };
    }


    public static abstract class MonitorEvent {
        public abstract boolean check();
        public abstract void run();
        public abstract int getMsgType();
        public boolean sendToMonitor() {
            if (hitalkMonitor.handler.hasMessages(getMsgType())){
                return false;
            }
            Message msg = hitalkMonitor.handler.obtainMessage(getMsgType());
            msg.obj = this;
            msg.sendToTarget();
            return true;
        }
    }

    public static void destory(){
        if (hitalkMonitor != null){
            hitalkMonitor.handler.removeCallbacksAndMessages(null);
        }
    }
}

package com.vivifram.second.hitalk.bean;

import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zuowei on 16-8-3.
 */
public class IMessageWrap<T extends AVIMTypedMessage> {

    public final static int MESSAGE_TYPE_TEXT = -1;
    public final static int MESSAGE_TYPE_IMAGE = -2;
    public final static int MESSAGE_TYPE_VOICE = -3;
    public final static int MESSAGE_TYPE_VIDEO = -4;
    public final static int MESSAGE_TYPE_LOCATION = -5;
    public final static int MESSAGE_TYPE_FILE = -6;

    public T message_;
    public OnIMessageStateChangedListener onIMessageStateChangedListener_;
    public interface OnIMessageStateChangedListener{
        void onIMessageStateChanged(AVIMMessage.AVIMMessageStatus status);
    }

    public static  List<IMessageWrap<AVIMTypedMessage>> buildFrom(List<AVIMMessage> avimMessages,boolean history){
        List<IMessageWrap<AVIMTypedMessage>> iMessageWraps = new ArrayList<>();
        if (avimMessages != null) {
            for (AVIMMessage avimMessage : avimMessages) {
                if (avimMessage instanceof AVIMTypedMessage){
                    IMessageWrap<AVIMTypedMessage> iMessageWrap = new IMessageWrap<>();
                    iMessageWrap.mHistory = history;
                    iMessageWrap.message_ = (AVIMTypedMessage) avimMessage;
                    iMessageWraps.add(iMessageWrap);
                }
            }
        }
        return iMessageWraps;
    }
    public static  List<IMessageWrap<AVIMTypedMessage>> buildFrom(AVIMMessage avimMessage,boolean history){
        List<IMessageWrap<AVIMTypedMessage>> iMessageWraps = new ArrayList<>();
        if (avimMessage != null) {
            IMessageWrap<AVIMTypedMessage> iMessageWrap = new IMessageWrap<>();
            iMessageWrap.mHistory = history;
            iMessageWrap.message_ = (AVIMTypedMessage) avimMessage;
            iMessageWraps.add(iMessageWrap);
        }
        return iMessageWraps;
    }

    public boolean mHistory;
}

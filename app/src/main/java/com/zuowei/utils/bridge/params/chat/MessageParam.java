package com.zuowei.utils.bridge.params.chat;

import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.zuowei.utils.bridge.constant.EaterAction;
import com.zuowei.utils.bridge.params.LightParam;

/**
 * Created by zuowei on 16-8-4.
 */
public class MessageParam extends LightParam {
    public static final int MESSAGE_ACTION_RECEIVED = 0x01;
    public static final int MESSAGE_ACTION_SEND_TEXT = 0x02;
    public static final int MESSAGE_ACTION_SEND_IMAGE = 0x03;
    public static final int MESSAGE_ACTION_SEND_LOCATION = 0x04;
    public static final int MESSAGE_ACTION_SEND_VOICE = 0x05;
    public static final int MESSAGE_ACTION_SEND_VIDEO = 0x06;
    public static final int MESSAGE_ACTION_SEND_FILE = 0x07;
    public AVIMTypedMessage message;
    public AVIMConversation conversation;
    public String messageText;
    public int mMessageAction;
    public MessageParam() {
        super(EaterAction.ACTION_DO_CHECK_MESSAGE);
    }

    public static MessageParam obtainTextMessage(String content, AVIMConversation conversation){
        MessageParam messageParam = new MessageParam();
        messageParam.mMessageAction = MESSAGE_ACTION_SEND_TEXT;
        messageParam.messageText = content;
        messageParam.conversation = conversation;
        return messageParam;
    }

    @Override
    public String toString() {
        return "MessageParam{" +
                "message=" + message +
                ", conversation=" + conversation +
                ", messageText='" + messageText + '\'' +
                ", mMessageAction=" + mMessageAction +
                '}';
    }
}

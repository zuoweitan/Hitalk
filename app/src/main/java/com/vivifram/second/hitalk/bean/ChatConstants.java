
package com.vivifram.second.hitalk.bean;

public class ChatConstants {
    public static final String PEER_ID = getPrefixConstant("peer_id");
    public static final String CONVERSATION_ID = getPrefixConstant("conversation_id");
    public static final String CHAT_NOTIFICATION_ACTION = getPrefixConstant("chat_notification_action");

    public ChatConstants() {
    }

    private static String getPrefixConstant(String str) {
        return "hitalk." + str;
    }
}

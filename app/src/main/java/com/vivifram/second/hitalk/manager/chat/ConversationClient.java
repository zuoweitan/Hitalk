package com.vivifram.second.hitalk.manager.chat;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;

import java.util.Arrays;

/**
 * Created by zuowei on 16-8-5.
 */
public class ConversationClient {

    private static ConversationClient sInstance;
    private static final String COLLEGE_PREFIX = "Hitalk_";

    private ConversationClient(){

    }

    public static ConversationClient getInstance(){
        if (sInstance == null) {
            synchronized (ConversationClient.class){
                if (sInstance == null) {
                    sInstance = new ConversationClient();
                }
            }
        }
        return sInstance;
    }

    public static String getCollegeSquareConversationName(String college){
        return COLLEGE_PREFIX + college;
    }

    public void getSquareConversationByName(String name,
                                                   AVIMConversationQueryCallback avimConversationQueryCallback){
        AVIMClient client = ClientManager.getInstance().getClient();
        if (client == null) {
            throw new RuntimeException("please open client first");
        }
        AVIMConversationQuery avimConversationQuery = client.getQuery();
        avimConversationQuery.whereEqualTo("name",name);
        avimConversationQuery.findInBackground(avimConversationQueryCallback);
    }


    public AVIMConversation getSquareConversationById(String conversationId){
        AVIMClient client = ClientManager.getInstance().getClient();
        if (client == null) {
            throw new RuntimeException("please open client first");
        }

        return client.getConversation(conversationId);
    }

    public static  void queryInSquare(String conversationId,
                                      AVIMConversationQueryCallback avimConversationQueryCallback) {
        final AVIMClient client = ClientManager.getInstance().getClient();
        AVIMConversationQuery conversationQuery = client.getQuery();
        conversationQuery.whereEqualTo("objectId", conversationId);
        conversationQuery.containsMembers(Arrays.asList(ClientManager.getInstance().getClientId()));
        conversationQuery.findInBackground(avimConversationQueryCallback);
    }
}

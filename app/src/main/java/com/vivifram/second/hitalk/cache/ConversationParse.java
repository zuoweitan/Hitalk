package com.vivifram.second.hitalk.cache;

import com.zuowei.dao.greendao.Conversation;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zuowei on 16-8-4.
 */
public class ConversationParse {

    public static Wrap parseFrom(String json){
        Wrap wrap = null;
        try {
            JSONObject jsonObject = new JSONObject(json);
            wrap = new Wrap();
            if (jsonObject.has("conversationId")){
                wrap.conversationId = jsonObject.getString("conversationId");
            }
            if (jsonObject.has("unreadcount")){
                wrap.unreadcount = jsonObject.getInt("unreadcount");
            }
            if (jsonObject.has("updateTime")){
                wrap.updateTime = jsonObject.getLong("updateTime");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return wrap;
    }

    public static String toJson(Wrap wrap){
        if (wrap != null) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("conversationId",wrap.conversationId);
                jsonObject.put("unreadcount",wrap.unreadcount);
                jsonObject.put("updateTime",wrap.updateTime);
                return jsonObject.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Conversation toConversationDao(Wrap wrap){
        Conversation conversation = new Conversation();
        conversation.setConversationId(wrap.conversationId);
        conversation.setContent(toJson(wrap));
        return conversation;
    }

    public static class Wrap implements Comparable<Wrap>{
        /*"conversation_id":"579175b78bada3390afc6682","unreadcount":0,"upadte_time":1469150689842*/
        public String conversationId;
        public int unreadcount;
        public long updateTime;

        public Wrap() {
        }

        public Wrap(String conversationId){
            this.conversationId = conversationId;
        }

        public String getConversationId() {
            return conversationId;
        }

        public int getUnreadcount() {
            return unreadcount;
        }

        public long getUpdateTime() {
            return updateTime;
        }

        @Override
        public int compareTo(Wrap another) {
            if (conversationId == null && another.conversationId == null) return 0;
            if (conversationId == null) return -1;
            if (another.conversationId == null) return 1;

            return conversationId.compareTo(another.conversationId);
        }
    }
}

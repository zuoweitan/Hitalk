package com.vivifram.second.hitalk.cache;

import android.content.Context;

import com.vivifram.second.hitalk.bean.Emojicon;
import com.zuowei.utils.bridge.EaterManager;
import com.zuowei.utils.bridge.params.SharePreferenceParam;
import com.zuowei.utils.handlers.SharePreferenceHandler;

import java.util.Map;

/**
 * Created by zuowei on 16-7-14.
 * for Hitalk static data.
 */
public class HiTalkModel {
    public static String APPID = "tul2H5WFbD4hMcI5I3aCJ3oW-gzGzoHsz";
    public static String APPKEY = "OnaWi9FMWsNrSu3u8pTpjilx";


    private static String SHARED_KEY_CURRENTUSER_USERNAME = "SHARED_KEY_CURRENTUSER_USERNAME";
    private static String SHARED_KEY_CURRENTUSER_NICK = "SHARED_KEY_CURRENTUSER_NICK";
    private static String SHARED_KEY_CURRENTUSER_AVATAR = "SHARED_KEY_CURRENTUSER_AVATAR";

    private static String CONVERSATION_COLLEGE_SQUARE_NAME = "CONVERSATION_COLLEGE_SQUARE_NAME";
    private static String CONVERSATION_COLLEGE_SQUARE_ID = "CONVERSATION_COLLEGE_SQUARE_ID";

    public interface EmojiconInfoProvider {
        Emojicon getEmojiconInfo(String emojiconIdentityCode);
        Map<String, Object> getTextEmojiconMapping();
    }

    private EmojiconInfoProvider mEmojiconInfoProvider;

    public EmojiconInfoProvider getEmojiconInfoProvider(){
        return mEmojiconInfoProvider;
    }

    public void setEmojiconInfoProvider(EmojiconInfoProvider emojiconInfoProvider){
        this.mEmojiconInfoProvider = emojiconInfoProvider;
    }

    protected Context mContext = null;

    public HiTalkModel(Context context){
        mContext = context;
    }

    public void setCurrentUserName(String userName){
        EaterManager.getInstance().broadcast(new SharePreferenceParam(SHARED_KEY_CURRENTUSER_USERNAME,userName));
    }

    public String getCurrentUsername(){
        return SharePreferenceHandler.getInstance().getString(SHARED_KEY_CURRENTUSER_USERNAME, null);
    }


    public void setCurrentNickName(String nickName){
        EaterManager.getInstance().broadcast(new SharePreferenceParam(SHARED_KEY_CURRENTUSER_NICK,nickName));
    }

    public String getCurrentNickname(){
        return SharePreferenceHandler.getInstance().getString(SHARED_KEY_CURRENTUSER_NICK, null);
    }

    public void setCurrentAvatar(String nickName){
        EaterManager.getInstance().broadcast(new SharePreferenceParam(SHARED_KEY_CURRENTUSER_AVATAR,nickName));
    }

    public String getCurrentAvatar(){
        return SharePreferenceHandler.getInstance().getString(SHARED_KEY_CURRENTUSER_AVATAR, null);
    }

    public void setSquareConversationName(String squareConversationName){
        EaterManager.getInstance().broadcast(new SharePreferenceParam(CONVERSATION_COLLEGE_SQUARE_NAME,squareConversationName));
    }

    public String getSquareConversationName(){
        return SharePreferenceHandler.getInstance().getString(CONVERSATION_COLLEGE_SQUARE_NAME, null);
    }

    public void setSquareConversationId(String squareConversationId){
        EaterManager.getInstance().broadcast(new SharePreferenceParam(CONVERSATION_COLLEGE_SQUARE_ID,squareConversationId));
    }

    public String getSquareConversationId(){
        return SharePreferenceHandler.getInstance().getString(CONVERSATION_COLLEGE_SQUARE_ID, null);
    }

}

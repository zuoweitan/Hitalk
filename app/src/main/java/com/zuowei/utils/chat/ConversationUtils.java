package com.zuowei.utils.chat;

import android.text.TextUtils;

import com.avos.avoscloud.AVCallback;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.zuowei.utils.helper.HiTalkHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 项目名称：Hitalk
 * 类描述：
 * 创建人：zuowei
 * 创建时间：16-10-25 下午7:35
 * 修改人：zuowei
 * 修改时间：16-10-25 下午7:35
 * 修改备注：
 */
public class ConversationUtils {
    public static void getConversationName(AVIMConversation conversation, final AVCallback<String> callback) {
        if(null != callback) {
            if(null == conversation) {
                callback.internalDone(null,new AVException(new Throwable("conversation can not be null!")));
            } else {
                if(conversation.isTransient()) {
                    callback.internalDone(conversation.getName(), null);
                } else if(2 == conversation.getMembers().size()) {
                    String peerId = getConversationPeerId(conversation);
                    LCIMProfileCache.getInstance().getUserName(peerId, callback);
                } else if(!TextUtils.isEmpty(conversation.getName())) {
                    callback.internalDone(conversation.getName(), null);
                } else {
                    LCIMProfileCache.getInstance().getCachedUsers(conversation.getMembers(), new AVCallback() {
                        protected void internalDone0(List<LCChatKitUser> lcimUserProfiles, AVException e) {
                            ArrayList nameList = new ArrayList();
                            if(null != lcimUserProfiles) {
                                Iterator i$ = lcimUserProfiles.iterator();

                                while(i$.hasNext()) {
                                    LCChatKitUser userProfile = (LCChatKitUser)i$.next();
                                    nameList.add(userProfile.getUserName());
                                }
                            }

                            callback.internalDone(TextUtils.join(",", nameList), e);
                        }
                    });
                }

            }
        }
    }

    public static void getConversationPeerIcon(AVIMConversation conversation, AVCallback<String> callback) {
        if(null != conversation && !conversation.isTransient() && !conversation.getMembers().isEmpty()) {
            String peerId = getConversationPeerId(conversation);
            if(1 == conversation.getMembers().size()) {
                peerId = conversation.getMembers().get(0);
            }

            LCIMProfileCache.getInstance().getUserAvatar(peerId, callback);
        } else {
            callback.internalDone(null, new AVException(new Throwable("cannot find icon!")));
        }

    }

    private static String getConversationPeerId(AVIMConversation conversation) {
        if(null != conversation && 2 == conversation.getMembers().size()) {
            String currentUserId = HiTalkHelper.getInstance().getCurrentUserId();
            String firstMemeberId = conversation.getMembers().get(0);
            return conversation.getMembers().get(firstMemeberId.equals(currentUserId)?1:0);
        } else {
            return "";
        }
    }
}

package com.zuowei.utils.chat;

import android.text.TextUtils;

import com.avos.avoscloud.AVCallback;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.vivifram.second.hitalk.manager.chat.ClientManager;
import com.vivifram.second.hitalk.model.ConversationType;
import com.zuowei.dao.greendao.User;
import com.zuowei.utils.common.NLog;
import com.zuowei.utils.common.TagUtil;
import com.zuowei.utils.helper.HiTalkHelper;
import com.zuowei.utils.helper.UserBeanCacheHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
                    NLog.i(TagUtil.makeTag(ConversationUtils.class),"conversation.isTransient");
                    callback.internalDone(conversation.getName(), null);
                } else if(2 == conversation.getMembers().size()) {
                    NLog.i(TagUtil.makeTag(ConversationUtils.class),"conversation.getMembers = 2");
                    String peerId = getConversationPeerId(conversation);
                    UserBeanCacheHelper.getInstance().getUserName(peerId, callback);
                } else if(!TextUtils.isEmpty(conversation.getName())) {
                    NLog.i(TagUtil.makeTag(ConversationUtils.class),"conversation.getName not null");
                    callback.internalDone(conversation.getName(), null);
                } else {
                    UserBeanCacheHelper.getInstance().getCachedUsers(conversation.getMembers(), new AVCallback<List<User>>() {
                        protected void internalDone0(List<User> users, AVException e) {
                            ArrayList nameList = new ArrayList();
                            if(null != users) {
                                Iterator<User> i$ = users.iterator();

                                while(i$.hasNext()) {
                                    User user = i$.next();
                                    nameList.add(user.getUserName());
                                }
                            }
                            callback.internalDone(TextUtils.join(",", nameList), e);
                        }
                    });
                }

            }
        }
    }

    public static void createGroupConversation(final List<String> memberIds, String name, final AVIMConversationCreatedCallback callback) {
        Map<String, Object> attrs = new HashMap<>();
        attrs.put(ConversationType.TYPE_KEY, ConversationType.Group.getValue());
        attrs.put("name", name);
        ClientManager.getInstance().getClient().createConversation(memberIds, "", attrs, false, true, callback);
    }

    public static void createSingleConversation(String memberId, AVIMConversationCreatedCallback callback) {
        Map<String, Object> attrs = new HashMap<>();
        attrs.put(ConversationType.TYPE_KEY, ConversationType.Single.getValue());
        ClientManager.getInstance().getClient().createConversation(Arrays.asList(memberId), "", attrs, false, true, callback);
    }

    public static void getConversationPeerIcon(AVIMConversation conversation, AVCallback<String> callback) {
        if(null != conversation && !conversation.isTransient() && !conversation.getMembers().isEmpty()) {
            String peerId = getConversationPeerId(conversation);
            if(1 == conversation.getMembers().size()) {
                peerId = conversation.getMembers().get(0);
            }
            UserBeanCacheHelper.getInstance().getUserAvatar(peerId, callback);
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

package com.vivifram.second.hitalk.ui.page.layout;

import com.avos.avoscloud.im.v2.AVIMMessage;

/**
 * Created by zuowei on 16-8-2.
 */
public interface OnChatItemClickListener {
    void onResendClick(AVIMMessage message);
    boolean onBubbleClick(AVIMMessage message);
    void onBubbleLongClick(AVIMMessage message);
    void onUserAvatarClick(String username);
}

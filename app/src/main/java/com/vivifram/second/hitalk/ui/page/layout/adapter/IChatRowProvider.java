package com.vivifram.second.hitalk.ui.page.layout.adapter;

import android.widget.BaseAdapter;

import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.vivifram.second.hitalk.bean.IMessageWrap;
import com.vivifram.second.hitalk.ui.page.layout.chat.HiChatRowLayout;

/**
 * Created by zuowei on 16-8-2.
 */
public interface IChatRowProvider {
    int getChatRowTypeCount();
    int getChatRowType(AVIMTypedMessage message);
    <T extends AVIMTypedMessage> HiChatRowLayout<T> getChatRow(IMessageWrap<T> messageWrap, int position, BaseAdapter adapter);
}

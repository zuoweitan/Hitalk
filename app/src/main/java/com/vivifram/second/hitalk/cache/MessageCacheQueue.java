package com.vivifram.second.hitalk.cache;

import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.vivifram.second.hitalk.bean.IMessageWrap;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by zuowei on 16-8-5.
 */
public class MessageCacheQueue extends ConcurrentLinkedQueue<List<IMessageWrap<AVIMTypedMessage>>> {
}

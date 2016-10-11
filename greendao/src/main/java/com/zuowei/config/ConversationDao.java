package com.zuowei.config;

import com.zuowei.func.Property;

/**
 * Created by zuowei on 16-8-4.
 */
public class ConversationDao {
    public static final String TABLE_NAME = "conversation";
    @Property(primaryKey = true,type = "text")
    public static final String COLUMN_NAME_CONVERSATIONID = "conversationId";
    @Property(type = "text")
    public static final String COLUMN_NAME_CONTENT = "content";
}

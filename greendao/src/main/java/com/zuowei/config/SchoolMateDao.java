package com.zuowei.config;

import com.zuowei.func.Property;

/**
 * Created by zuowei on 16-8-25.
 */
public class SchoolMateDao {
    public static final String TABLE_NAME = "schoolmate";
    @Property(primaryKey = true,type = "text")
    public static final String COLUMN_NAME_USER_ID = "userId";
    @Property(notNull = true,type = "text")
    public static final String COLUMN_NAME_RELEATED_USER_ID = "releatedId";
    @Property(type = "integer")
    public static final String COLUMN_NAME_FRIEND_STATE = "friendState";
    @Property(type = "date")
    public static final String COLUMN_CREATE_AT = "createAt";
}

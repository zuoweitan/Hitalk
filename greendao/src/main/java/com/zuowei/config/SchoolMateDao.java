package com.zuowei.config;

import com.zuowei.func.Property;

/**
 * Created by zuowei on 16-8-25.
 */
public class SchoolMateDao {
    public static final String TABLE_NAME = "schoolmate";
    @Property(primaryKey = true,type = "text")
    public static final String COLUMN_NAME_USEER_ID = "userId";
    @Property(type = "integer")
    public static final String COLUMN_NAME_FRIEND_STATE = "friendState";
    @Property(type = "date")
    public static final String COLUMN_CREATE_AT = "createAt";
}

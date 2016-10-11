package com.zuowei.config;

import com.zuowei.func.Property;

/**
 * Created by zuowei on 16-8-25.
 */
public class BaseBeanDao {
    public static final String TABLE_NAME = "bean";
    @Property(primaryKey = true,type = "text")
    public static final String COLUMN_NAME_LOCALID = "localId";
    @Property(type = "text")
    public static final String COLUMN_NAME_CONTENT = "content";
    @Property(type = "text")
    public static final String COLUMN_NAME_CLASS = "className";
    @Property(type = "integer")
    public static final String COLUMN_NAME_COMMAND_TYPE = "commandType";
    @Property(type = "date")
    public static final String COLUMN_CREATE_AT = "createAt";
}

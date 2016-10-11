package com.zuowei.utils.common;

/**
 * Created by zuowei on 16-7-12.
 */
public class TagUtil {

    private static final String PREFIX = "hitalk_";
    private static final String ACTION_PREFIX = "action_to_";
    public static String makeTag(Class c){
        if (c != null){
            return PREFIX + c.getSimpleName();
        }
        return PREFIX;
    }

    public static String makeActionTag(Class c){
        if (c != null){
            return ACTION_PREFIX + c.getSimpleName();
        }
        return ACTION_PREFIX;
    }
}

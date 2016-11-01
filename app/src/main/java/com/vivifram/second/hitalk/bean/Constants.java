package com.vivifram.second.hitalk.bean;

import com.zuowei.utils.common.DesUtils;

/**
 * 项目名称：Hitalk
 * 类描述：
 * 创建人：zuowei
 * 创建时间：16-10-31 下午9:26
 * 修改人：zuowei
 * 修改时间：16-10-31 下午9:26
 * 修改备注：
 */
public class Constants {
    public static final String BN_PHOTOS_KEY = "HITALK_BN_PHOTOS_KEY";
    public static final DesUtils BN_PHOTOS_DESUTIL = new DesUtils(BN_PHOTOS_KEY);

    public static final String INVITATION_ACTION = "invitation_action";


    //Notification
    private static final String LEANMESSAGE_CONSTANTS_PREFIX = "com.avoscloud.leanchatlib.";
    public static final String NOTOFICATION_TAG = getPrefixConstant("notification_tag");
    public static final String NOTIFICATION_SINGLE_CHAT = Constants.getPrefixConstant("notification_single_chat");
    public static final String NOTIFICATION_GROUP_CHAT = Constants.getPrefixConstant("notification_group_chat");
    public static final String NOTIFICATION_SYSTEM = Constants.getPrefixConstant("notification_system_chat");

    public static String getPrefixConstant(String str) {
        return LEANMESSAGE_CONSTANTS_PREFIX + str;
    }
}

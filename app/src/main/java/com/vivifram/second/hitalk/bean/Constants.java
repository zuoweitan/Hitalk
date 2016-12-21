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

    public final class User{
        public static final String OBJECTID_C = "objectId";
        public static final String COLLEGECODE_C = "collegeCode";
        public static final String NICKNAME_C = "nickName";
        public static final String COLLEGE_C = "college";
    }

    public final class BlurDialog{
        public static final String BUNDLE_KEY_DOWN_SCALE_FACTOR = "bundle_key_down_scale_factor";

        public static final String BUNDLE_KEY_BLUR_RADIUS = "bundle_key_blur_radius";

        public static final String BUNDLE_KEY_DIMMING = "bundle_key_dimming_effect";

        public static final String BUNDLE_KEY_USE_RENDERSCRIPT = "bundle_key_use_renderscript";

        public static final String BUNDLE_KEY_DEBUG = "bundle_key_debug_effect";


        //DEFAULT
        public static final int DEFAULT_RADIUS = 9;
        public static final float DEFAULT_FACTOR = 4.0f;
        public static final boolean DEFAULT_DIMMING = true;
        public static final boolean DEFAULT_USE_RENDERSCRIPT = true;
    }

    public final class SearchType{
        public static final String SEARCH_TYPE = "SEARCH_TYPE";
        public static final int SEARCH_FRIEND_TYPE =  0x01;
    }

}

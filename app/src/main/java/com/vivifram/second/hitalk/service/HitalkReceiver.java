package com.vivifram.second.hitalk.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.vivifram.second.hitalk.bean.Constants;
import com.vivifram.second.hitalk.manager.chat.PushManager;
import com.zuowei.utils.bridge.EaterManager;
import com.zuowei.utils.bridge.params.push.InvitationParam;
import com.zuowei.utils.common.NLog;
import com.zuowei.utils.common.NotificationUtils;
import com.zuowei.utils.common.TagUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 项目名称：Hitalk
 * 类描述：
 * 创建人：zuowei
 * 创建时间：16-10-31 下午9:25
 * 修改人：zuowei
 * 修改时间：16-10-31 下午9:25
 * 修改备注：
 */
public class HitalkReceiver extends BroadcastReceiver {

    public final static String AVOS_DATA = "com.avoscloud.Data";

    @Override
    public void onReceive(Context context, Intent intent) {
        NLog.i(TagUtil.makeTag(getClass()),"HitalkReceiver onReceive");
        String action = intent.getAction();
        if (!TextUtils.isEmpty(action)) {
            if (action.equals(Constants.INVITATION_ACTION)) {
                String avosData = intent.getStringExtra(AVOS_DATA);
                if (!TextUtils.isEmpty(avosData)) {
                    try {
                        JSONObject json = new JSONObject(avosData);
                        if (null != json) {
                            String alertStr = json.getString(PushManager.AVOS_ALERT);

                            Intent notificationIntent = new Intent(context, NotificationBroadcastReceiver.class);
                            notificationIntent.putExtra(Constants.NOTOFICATION_TAG, Constants.NOTIFICATION_SYSTEM);
                            NotificationUtils.showNotification(context, "Hitalk", alertStr, notificationIntent);
                        }
                    } catch (JSONException e) {
                        NLog.e(TagUtil.makeTag(getClass()),e);
                    }
                }
            }
        }
        EaterManager.getInstance().broadCastSticky(new InvitationParam());
    }
}

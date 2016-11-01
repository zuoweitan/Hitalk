package com.vivifram.second.hitalk.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.vivifram.second.hitalk.bean.Constants;
import com.vivifram.second.hitalk.manager.chat.ClientManager;
import com.vivifram.second.hitalk.ui.LoginActivity;
import com.zuowei.utils.common.UIUtils;


public class NotificationBroadcastReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    if (null == ClientManager.getInstance().getClient()) {
      gotoLoginActivity(context);
    } else {
      String tag = intent.getStringExtra(Constants.NOTOFICATION_TAG);
      if (Constants.NOTIFICATION_GROUP_CHAT.equals(tag)) {
        gotoChatActivity(context, intent);
      } else if (Constants.NOTIFICATION_SINGLE_CHAT.equals(tag)) {
        gotoChatActivity(context, intent);
      } else if (Constants.NOTIFICATION_SYSTEM.equals(tag)) {
        gotoNewFriendActivity(context,intent);
      }
    }
  }

  private void gotoLoginActivity(Context context) {
    UIUtils.startActivitySafety(context,LoginActivity.class);
  }

  private void gotoChatActivity(Context context, Intent intent) {
  }

  private void gotoNewFriendActivity(Context context, Intent intent) {
  }
}

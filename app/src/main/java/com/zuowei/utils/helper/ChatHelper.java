package com.zuowei.utils.helper;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVCallback;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.bumptech.glide.Glide;
import com.vivifram.second.hitalk.HiTalkApplication;
import com.vivifram.second.hitalk.R;
import com.zuowei.dao.greendao.User;
import com.zuowei.utils.common.GlideCircleTransform;
import com.zuowei.utils.common.TagUtil;

/**
 * Created by zuowei on 16-8-2.
 */
public class ChatHelper {
    private static final String TAG = TagUtil.makeTag(ChatHelper.class);
    private static ChatHelper sInstance;
    private ChatHelper (){}

    public static ChatHelper getInstance(){
        if (sInstance == null){
            synchronized (ChatHelper.class){
                if (sInstance == null){
                    sInstance = new ChatHelper();
                }
            }
        }
        return sInstance;
    }

    public boolean isMe(AVIMMessage message){
        return HiTalkHelper.getInstance().getCurrentUserId().equals(message.getFrom());
    }

    /**
     * 设置用户头像
     */
    public static void setUserAvatar(final Context context, String userId, final ImageView imageView){
        setUserAvatar(context,userId,imageView,false);
    }

    public static void setUserAvatar(final Context context, String userId, final ImageView imageView, final boolean circle){
        UserBeanCacheHelper.getInstance().getCachedUser(userId, new AVCallback<User>() {
            @Override
            protected void internalDone0(User user, AVException e) {
                if (user != null){
                    try {
                        if (circle){
                            Glide.with(context).load(user.getAvatar())
                                    .placeholder(R.color.bg_no_photo)
                                    .error(R.drawable.default_avatar)
                                    .transform(new GlideCircleTransform(context))
                                    .into(imageView);
                        }else {
                            Glide.with(context).load(user.getAvatar())
                                    .error(R.drawable.default_avatar)
                                    .placeholder(new ColorDrawable(
                                            HiTalkApplication.mAppContext.getResources().getColor(R.color.bg_no_photo)))
                                    .into(imageView);
                        }

                    }catch (Exception err){
                        imageView.setImageResource(R.drawable.default_avatar);
                    }
                }else {
                    imageView.setImageResource(R.drawable.default_avatar);
                }
            }
        });
    }


    /**
     * 设置用户昵称
     */
    public static void setUserNick(String userId, final TextView textView){
        UserBeanCacheHelper.getInstance().getCachedUser(userId, new AVCallback<User>() {
            @Override
            protected void internalDone0(User user, AVException e) {
                if (e == null && user != null){
                    textView.setText(user.getNick());
                }
            }
        });
    }
}

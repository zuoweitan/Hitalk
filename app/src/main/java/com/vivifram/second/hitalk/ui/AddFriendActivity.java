package com.vivifram.second.hitalk.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.avos.avoscloud.AVUser;
import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.base.BaseActivity;
import com.vivifram.second.hitalk.base.LayoutInject;
import com.vivifram.second.hitalk.bean.Constants;
import com.vivifram.second.hitalk.broadcast.ConnectivityNotifier;
import com.vivifram.second.hitalk.ui.dialog.BaseBlurDialog;
import com.vivifram.second.hitalk.ui.dialog.QRDialog;
import com.vivifram.second.hitalk.ui.layout.AddFriendLayout;
import com.zuowei.utils.common.DisplayUtil;
import com.zuowei.utils.common.NLog;
import com.zuowei.utils.common.NToast;
import com.zuowei.utils.common.TagUtil;
import com.zuowei.utils.helper.HiTalkHelper;
import com.zuowei.utils.helper.UserCacheHelper;

import bolts.Continuation;
import bolts.Task;
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;

/**
 * 项目名称：Hitalk
 * 类描述：
 * 创建人：zuowei
 * 创建时间：16-12-8 下午3:34
 * 修改人：zuowei
 * 修改时间：16-12-8 下午3:34
 * 修改备注：
 */
@LayoutInject(name = "AddFriendLayout")
public class AddFriendActivity extends BaseActivity<AddFriendLayout>{

    public static void start(Context c){
        Intent intent = new Intent(c,AddFriendActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        c.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_add_friend_layout);
        mLayout.setOnTitleActionListener(onTitleActionListener);
    }


    private AddFriendLayout.OnTitleActionListener onTitleActionListener
            = new AddFriendLayout.OnTitleActionListener() {
        @Override
        public void onBack() {
            finish();
        }

        @Override
        public void showQR() {
            NLog.i(TagUtil.makeTag(getClass()),"start showQR");
            if (ConnectivityNotifier.isConnected(mAppCtx)) {
                generateAndShowQR();
            }else {
                NToast.shortToast(mAppCtx,R.string.network_unavailable);
            }
        }
    };

    private void generateAndShowQR() {
        String userId = HiTalkHelper.getInstance().getCurrentUserId();
        Task.callInBackground(() -> {
            Bitmap logo = BitmapFactory.decodeResource(getResources(),R.drawable.hitalk);
            return QRCodeEncoder.syncEncodeQRCode(userId, DisplayUtil.dip2px(150)
                    ,getResources().getColor(R.color.hitalk_yellow),logo);
        }).continueWith(new Continuation<Bitmap, Void>() {
            @Override
            public Void then(Task<Bitmap> task) throws Exception {
                Bitmap result = task.getResult();
                NLog.i(TagUtil.makeTag(getClass()),"generateAndShowQR and result = "+result);
                if (result != null){
                    showQRDialog(result);
                }else {
                    NToast.shortToast(mAppCtx,R.string.generate_qr_failed);
                }
                return null;
            }
        },Task.UI_THREAD_EXECUTOR);
    }

    private void showQRDialog(Bitmap qr) {
        QRDialog qrDialog = BaseBlurDialog.newInstance(QRDialog.class
                , Constants.BlurDialog.DEFAULT_RADIUS
                , Constants.BlurDialog.DEFAULT_FACTOR
                , Constants.BlurDialog.DEFAULT_DIMMING
                , Constants.BlurDialog.DEFAULT_USE_RENDERSCRIPT
                , false);


        AVUser avUser = HiTalkHelper.getInstance().getCurrentUser();


        qrDialog.show(getFragmentManager(),"Hitalk");
        qrDialog.setCallback(()-> qrDialog.setAvatar(UserCacheHelper.getInstance().getAvatarUrl(avUser))
                .setNick((String) avUser.get(Constants.User.NICKNAME_C))
                .setCollege((String) avUser.get(Constants.User.COLLEGE_C))
                .setQR(qr));
    }
}

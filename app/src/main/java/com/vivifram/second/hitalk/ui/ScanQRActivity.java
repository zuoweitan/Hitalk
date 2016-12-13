package com.vivifram.second.hitalk.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;

import com.vivifram.second.hitalk.HiTalkApplication;
import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.base.BaseActivity;
import com.vivifram.second.hitalk.base.InterfaceInject;
import com.vivifram.second.hitalk.base.LayoutInject;
import com.vivifram.second.hitalk.ui.layout.ScanQRLayout;
import com.zuowei.utils.common.NToast;

import cn.bingoogolapple.qrcode.core.QRCodeView;


/**
 * 项目名称：Hitalk
 * 类描述：
 * 创建人：zuowei
 * 创建时间：16-12-13 下午2:23
 * 修改人：zuowei
 * 修改时间：16-12-13 下午2:23
 * 修改备注：
 */
@LayoutInject(name = "ScanQRLayout")
public class ScanQRActivity extends BaseActivity<ScanQRLayout>{

    public static void start(Context ctx){
        start(ctx,ScanQRActivity.class);
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_scan_qr_layout);
    }


    @InterfaceInject(bindName = "onTitleActionListener")
    ScanQRLayout.OnTitleActionListener onTitleActionListener = this::finish;

    @InterfaceInject(bindName = "delegate")
    QRCodeView.Delegate delegate = new QRCodeView.Delegate() {
        @Override
        public void onScanQRCodeSuccess(String result) {
            vibrate();
            mLayout.reScan();
            NToast.shortToast(HiTalkApplication.mAppContext,"onScanQRCodeSuccess result = "+result);
        }

        @Override
        public void onScanQRCodeOpenCameraError() {
            NToast.shortToast(HiTalkApplication.mAppContext,R.string.open_camera_error);
        }
    };

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mLayout.startScan();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLayout.stopScan();
    }
}


package com.vivifram.second.hitalk.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Vibrator;

import com.vivifram.second.hitalk.HiTalkApplication;
import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.base.BaseActivity;
import com.vivifram.second.hitalk.base.InterfaceInject;
import com.vivifram.second.hitalk.base.LayoutInject;
import com.vivifram.second.hitalk.ui.layout.ScanQRLayout;
import com.zuowei.qrcode.activity.CaptureFragment;
import com.zuowei.qrcode.activity.CodeUtils;
import com.zuowei.utils.common.NToast;


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

    private CaptureFragment captureFragment;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_scan_qr_layout);

        captureFragment = new CaptureFragment();
        CodeUtils.setFragmentArgs(captureFragment, R.layout.scanqr_layout);
        captureFragment.setAnalyzeCallback(analyzeCallback);
        mLayout.bindScanFragment(getSupportFragmentManager(),captureFragment);
    }

    private void reScan(){
        if (captureFragment != null) {
            captureFragment.getHandler().sendEmptyMessage(com.zuowei.qrcode.R.id.restart_preview);
        }
    }

    @InterfaceInject(bindName = "onTitleActionListener")
    ScanQRLayout.OnTitleActionListener onTitleActionListener = this::finish;

    CodeUtils.AnalyzeCallback analyzeCallback = new CodeUtils.AnalyzeCallback() {
        @Override
        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
            vibrate();
            reScan();
            NToast.shortToast(HiTalkApplication.mAppContext,"onScanQRCodeSuccess result = "+result);
        }

        @Override
        public void onAnalyzeFailed() {
            NToast.shortToast(HiTalkApplication.mAppContext,R.string.open_camera_error);
        }
    };

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }
}


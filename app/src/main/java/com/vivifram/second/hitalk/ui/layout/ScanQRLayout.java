package com.vivifram.second.hitalk.ui.layout;

import android.view.View;

import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.ui.view.BGATitlebar;

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
public class ScanQRLayout extends BaseLayout {

    public ScanQRLayout(View rootView) {
        super(rootView);
    }

    public interface OnTitleActionListener{
        void onBack();
    }

    private BGATitlebar titlebar;
    private QRCodeView qrCodeView;
    private QRCodeView.Delegate delegate;
    private OnTitleActionListener onTitleActionListener;

    @Override
    public void onContentViewCreate(View view) {
        super.onContentViewCreate(view);
        titlebar = (BGATitlebar) findViewById(R.id.titleBar);
        titlebar.setDelegate(new BGATitlebar.BGATitlebarDelegate(){
            @Override
            public void onClickLeftCtv() {
                if (onTitleActionListener != null) {
                    onTitleActionListener.onBack();
                }
            }
        });

        qrCodeView = (QRCodeView) findViewById(R.id.zbarview);
        qrCodeView.setDelegate(delegate);
    }

    public void startScan(){
        if (qrCodeView != null) {
            qrCodeView.startSpotDelay(3000);
        }
    }

    public void reScan(){
        if (qrCodeView != null) {
            qrCodeView.startSpot();
        }
    }

    public void stopScan(){
        if (qrCodeView != null) {
            qrCodeView.stopCamera();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (qrCodeView != null) {
            qrCodeView.showScanRect();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (qrCodeView != null) {
            qrCodeView.onDestroy();
        }
    }
}

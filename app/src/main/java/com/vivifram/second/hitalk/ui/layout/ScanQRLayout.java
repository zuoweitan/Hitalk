package com.vivifram.second.hitalk.ui.layout;

import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.FrameLayout;

import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.ui.view.BGATitlebar;
import com.zuowei.qrcode.activity.CaptureFragment;


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
    }

    public void bindScanFragment(FragmentManager fragmentManager, CaptureFragment captureFragment) {
        fragmentManager.beginTransaction().replace(R.id.qrscaner_container, captureFragment).commit();
    }
}

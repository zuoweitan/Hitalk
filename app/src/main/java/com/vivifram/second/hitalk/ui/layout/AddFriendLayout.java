package com.vivifram.second.hitalk.ui.layout;

import android.view.View;

import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.base.BindView;
import com.vivifram.second.hitalk.ui.view.BGATitlebar;
import com.zuowei.utils.helper.ViewClickHelper;

/**
 * 项目名称：Hitalk
 * 类描述：
 * 创建人：zuowei
 * 创建时间：16-12-8 下午3:35
 * 修改人：zuowei
 * 修改时间：16-12-8 下午3:35
 * 修改备注：
 */
public class AddFriendLayout extends BaseLayout{

    @BindView(id = R.id.scanQrRl, boundClick = true)
    private View scanQrV;
    @BindView(id = R.id.addressRl, boundClick = true)
    private View addressV;
    @BindView(id = R.id.searchLt)
    private View searchLt;
    @BindView(id = R.id.titleBar)
    private BGATitlebar titlebar;

    public interface OnTitleActionListener{
        void onBack();
        void showQR();
    }

    public interface OnItemClickListener{
        void onScanQr();
        void onAddAddress();
    }

    public interface OnSearchItemClickListener{
        void onSearch();
    }

    public AddFriendLayout(View rootView) {
        super(rootView);
    }

    private OnTitleActionListener onTitleActionListener;
    private OnItemClickListener onItemClickListener;
    private OnSearchItemClickListener onSearchItemClickListener;

    @Override
    public void onContentViewCreate(View view) {
        super.onContentViewCreate(view);

        titlebar.setDelegate(new BGATitlebar.BGATitlebarDelegate(){
            @Override
            public void onClickLeftCtv() {
                super.onClickLeftCtv();
                if (onTitleActionListener != null) {
                    onTitleActionListener.onBack();
                }
            }

            @Override
            public void onClickRightCtv() {
                super.onClickRightCtv();
                if (onTitleActionListener != null) {
                    onTitleActionListener.showQR();
                }
            }
        });

        searchLt.setOnClickListener(new ViewClickHelper(false) {
            @Override
            public void onRealClick(View v) {
                if (onSearchItemClickListener != null) {
                    onSearchItemClickListener.onSearch();
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.scanQrRl:
                if (onItemClickListener != null) {
                    onItemClickListener.onScanQr();
                }
                break;
            case R.id.addressRl:
                if (onItemClickListener != null) {
                    onItemClickListener.onAddAddress();
                }
                break;
        }
    }

    public View getShareElement(){
        return searchLt;
    }

    public AddFriendLayout setOnTitleActionListener(OnTitleActionListener onTitleActionListener) {
        this.onTitleActionListener = onTitleActionListener;
        return this;
    }

    public AddFriendLayout setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        return this;
    }
}

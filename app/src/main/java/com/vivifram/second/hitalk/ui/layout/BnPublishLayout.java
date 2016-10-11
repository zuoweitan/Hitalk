package com.vivifram.second.hitalk.ui.layout;

import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.ui.layout.adapter.BnImagesAdapter;
import com.vivifram.second.hitalk.ui.springview.container.DefaultFooter;
import com.vivifram.second.hitalk.ui.springview.container.DefaultHeader;
import com.vivifram.second.hitalk.ui.springview.widget.SpringView;
import com.vivifram.second.hitalk.ui.view.BGATitlebar;
import com.zuowei.utils.common.NToast;

import java.util.ArrayList;

/**
 * Created by zuowei on 16-8-15.
 */
public class BnPublishLayout extends BaseLayout {


    public interface OnTitleClickListener{
        void onLeftClick();
        void onCenterClick();
        void onRightClick();
    }
    private RecyclerView mImagesRv;
    private SpringView mSv;
    private BGATitlebar mTitleBB;
    private EditText mBnpEt;
    private BnImagesAdapter mBnImagesAdapter;
    private ArrayList<String> mSelectedImages;
    private OnTitleClickListener onTitleClickListener;
    public BnPublishLayout(View rootView) {
        super(rootView);
    }

    @Override
    public void onContentViewCreate(View view) {
        super.onContentViewCreate(view);
        mImagesRv = (RecyclerView) findViewById(R.id.imageRv);
        mSv = (SpringView) findViewById(R.id.bnPublishSv);
        mTitleBB = (BGATitlebar) findViewById(R.id.titleBar);
        mTitleBB.setDelegate(new BGATitlebar.BGATitlebarDelegate(){
            @Override
            public void onClickTitleCtv() {
                super.onClickTitleCtv();
                if (onTitleClickListener != null){
                    onTitleClickListener.onCenterClick();
                }
            }

            @Override
            public void onClickLeftCtv() {
                super.onClickLeftCtv();
                if (onTitleClickListener != null){
                    onTitleClickListener.onLeftClick();
                }
            }

            @Override
            public void onClickRightCtv() {
                super.onClickRightCtv();
                if (TextUtils.isEmpty(getContent())){
                    NToast.shortToast(mAppCtx,mRes.getString(R.string.bnContentNull));
                    return;
                }
                if (onTitleClickListener != null){
                    onTitleClickListener.onRightClick();
                }
            }
        });
        mBnpEt = (EditText) findViewById(R.id.bnInfoEt);


        mBnImagesAdapter = new BnImagesAdapter(mCtx);

        mImagesRv.setLayoutManager(new StaggeredGridLayoutManager(4, OrientationHelper.VERTICAL));
        mImagesRv.setAdapter(mBnImagesAdapter);
        mImagesRv.setHasFixedSize(true);

        mBnImagesAdapter.setPhotoPaths(new ArrayList<String>());

        mSv.setHeader(new DefaultHeader());
        mSv.setFooter(new DefaultFooter());
        mSv.setGive(SpringView.Give.NONE);
        mSv.setType(SpringView.Type.FOLLOW);

    }

    public void setImageItemOnClickListener(BnImagesAdapter.OnImageItemOnClickListener onImageItemOnClickListener){
        if (mBnImagesAdapter != null) {
            mBnImagesAdapter.setOnImageItemOnClickListener(onImageItemOnClickListener);
        }
    }

    public void setSelectedImages(ArrayList<String> mSelectedImages) {
        if (mSelectedImages == null){
            mSelectedImages = new ArrayList<>();
        }
        this.mSelectedImages = mSelectedImages;
        if (mBnImagesAdapter != null) {
            mBnImagesAdapter.setPhotoPaths(mSelectedImages);
        }
    }

    public ArrayList<String> getSelectedImages() {
        if (mSelectedImages == null){
            mSelectedImages = new ArrayList<>();
        }
        return mSelectedImages;
    }

    public void setOnTitleClickListener(OnTitleClickListener onTitleClickListener) {
        this.onTitleClickListener = onTitleClickListener;
    }

    public String getContent(){
        return mBnpEt.getText()+"";
    }
}
